package com.fh.shop.api.cart.biz;

import com.alibaba.fastjson.JSON;
import com.fh.shop.api.cart.vo.CartItemVo;
import com.fh.shop.api.cart.vo.CartVo;
import com.fh.shop.api.goods.IGoodsFeignService;
import com.fh.shop.api.goods.po.Sku;
import com.fh.shop.common.Constants;
import com.fh.shop.common.KeyUtil;
import com.fh.shop.common.ResponseEnum;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.util.BigDecimalUtil;
import com.fh.shop.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service("cartService")
public class ICartServiceImpl implements ICartService {

    @Resource
    private IGoodsFeignService goodsFeignService;


    @Value("${sku.count.limit}")
    private int skuCount;
    @Override
    public ServerResponse addItem(Long memberId, Long skuId, Long count) {
        if(count>skuCount){
            return ServerResponse.error(ResponseEnum.CART_COUNT_LIMIT);
        }

        //判断是否有该商品
//        Sku skuDB = skuMapper.selectById(skuId);
        ServerResponse<Sku> skuResponse = goodsFeignService.findSku(skuId);
        Sku skuDB = skuResponse.getData();

        if(skuDB==null){
            return ServerResponse.error(ResponseEnum.CART_SKU_IS_NULL);
        }
        //判断商品是否上架
        Integer status = skuDB.getStatus();
        if(status==Constants.STATUS_DOWN){
            return ServerResponse.error(ResponseEnum.CART_SKU_IS_DOWN);
        }

        //商品的购买数量是否大于库存数量
        if(skuDB.getStock() < count){
            return ServerResponse.error(ResponseEnum.CART_SKU_STOCK_ERROR);
        }

        //该会员是否有购物车
        String key = KeyUtil.buildCartKey(memberId);
        String cartJson = RedisUtil.hget(key,Constants.CART_JSON_FIELD);
        if(StringUtils.isEmpty(cartJson)){
            //没有购物车，创建一个购物车，并把该商品添加进去
            if(count<0){
                return ServerResponse.error(ResponseEnum.CART_IS_ERROR);
            }
            if(count>skuCount){
                return ServerResponse.error(ResponseEnum.CART_COUNT_LIMIT);
            }
            CartVo cartVo = new CartVo();
            CartItemVo cartItemVo = new CartItemVo();
            cartItemVo.setSkuName(skuDB.getSkuName());
            cartItemVo.setPrice(skuDB.getPrice().toString());
            cartItemVo.setCount(count);
            cartItemVo.setSkuId(skuDB.getId());
            cartItemVo.setSkuImage(skuDB.getColorImage());
            BigDecimal subPrice = BigDecimalUtil.mul(count + "", skuDB.getPrice().toString());
            cartItemVo.setSubPrice(subPrice.toString());

            cartVo.getCartItemVoList().add(cartItemVo);
            cartVo.setTotalCount(count);
            cartVo.setTotalPrice(subPrice.toString());
            String cartVoJson = JSON.toJSONString(cartVo);
//            RedisUtil.set(key ,cartVoJson);
            RedisUtil.hset(key,Constants.CART_JSON_FIELD,cartVoJson);
            RedisUtil.hset(key,Constants.CART_COUNT_FIELD,cartVo.getTotalCount()+"");
        }else {
            //如果已有购物车
            //将通过key获取的购物车对象转换为json对象
            CartVo cartVo = JSON.parseObject(cartJson, CartVo.class);
            //获取购物车中的每一件商品
            List<CartItemVo> cartItemVoList = cartVo.getCartItemVoList();
            //循环购物车中的商品和新增的商品比较是否已经存在
            Optional<CartItemVo> item = cartItemVoList.stream().filter(x -> x.getSkuId().longValue() == skuId.longValue()).findFirst();
            if(item.isPresent()){
            //购物车已经有这款商品，找到这款商品，更新这款商品的小计，数量
                CartItemVo cartItemVo = item.get();
                long itemCount = cartItemVo.getCount() + count;
                if(itemCount>skuCount){
                    return ServerResponse.error(ResponseEnum.CART_COUNT_LIMIT);
                }
                if(itemCount<1){
                    cartItemVoList.removeIf(x -> x.getSkuId().longValue()==cartItemVo.getSkuId().longValue());

                    if(cartItemVoList.size()==0){
                        RedisUtil.del(key);
                        return ServerResponse.success();
                    }
                    updateCart(key, cartVo);
                    return ServerResponse.success();
                }

                cartItemVo.setCount(itemCount);
                BigDecimal subPrice = new BigDecimal(cartItemVo.getSubPrice());
                String subPriceStr = subPrice.add(BigDecimalUtil.mul(cartItemVo.getPrice(), count + "")).toString();

                cartItemVo.setSubPrice(subPriceStr);
                //更新购物车
                updateCart(key, cartVo);
            }else{
                //如果购物车没有这款商品，将这款商品直接加入购物车
                if(count<0){
                    return ServerResponse.error(ResponseEnum.CART_IS_ERROR);
                }
                if(count>skuCount){
                    return ServerResponse.error(ResponseEnum.CART_COUNT_LIMIT);
                }
                CartItemVo cartItemVo = new CartItemVo();
                cartItemVo.setSkuName(skuDB.getSkuName());
                cartItemVo.setPrice(skuDB.getPrice().toString());
                cartItemVo.setCount(count);
                cartItemVo.setSkuId(skuDB.getId());
                cartItemVo.setSkuImage(skuDB.getColorImage());
                BigDecimal subPrice = BigDecimalUtil.mul(count + "", skuDB.getPrice().toString());
                cartItemVo.setSubPrice(subPrice.toString());
                //之前的商品值加上新赋的商品值
                cartVo.getCartItemVoList().add(cartItemVo);
                //循环重新设置总价和商品数量
                updateCart(key, cartVo);

            }


        }
        return ServerResponse.success();
    }

    private void updateCart(String key, CartVo cartVo) {
        List<CartItemVo> cartItemVos = cartVo.getCartItemVoList();
        long totalCount = 0;
        BigDecimal totalPrice = new BigDecimal(0);
        for (CartItemVo itemVo : cartItemVos) {
            totalCount += itemVo.getCount();
            totalPrice = totalPrice.add(new BigDecimal(itemVo.getSubPrice()));
        }
        cartVo.setTotalCount(totalCount);
        cartVo.setTotalPrice(totalPrice.toString());
        String cartVoJson = JSON.toJSONString(cartVo);
        //放入redis中
        RedisUtil.hset(key,Constants.CART_JSON_FIELD,cartVoJson);
        RedisUtil.hset(key,Constants.CART_COUNT_FIELD,cartVo.getTotalCount()+"");
    }

    @Override
    public ServerResponse findCart(Long id) {
        String cartKey = KeyUtil.buildCartKey(id);
        String cartJson = RedisUtil.hget(cartKey, Constants.CART_JSON_FIELD);
        if(StringUtils.isEmpty(cartJson)){
            ServerResponse.error();
        }
        CartVo cartVo = JSON.parseObject(cartJson, CartVo.class);
        return ServerResponse.success(cartVo);
    }

    @Override
    public ServerResponse findCartCount(Long id) {
        String cartKey = KeyUtil.buildCartKey(id);
        String cartCountJson = RedisUtil.hget(cartKey, Constants.CART_COUNT_FIELD);
        if (StringUtils.isEmpty(cartCountJson)) {
            ServerResponse.error();
        }

        return ServerResponse.success(cartCountJson);
    }

    @Override
    public ServerResponse deleteCart(Long id, Long skuId) {

        String key = KeyUtil.buildCartKey(id);
        String cartRedis = RedisUtil.hget(key, Constants.CART_JSON_FIELD);
        CartVo cartVo = JSON.parseObject(cartRedis, CartVo.class);
        List<CartItemVo> cartItemVoList = cartVo.getCartItemVoList();
        Optional<CartItemVo> itemVo = cartItemVoList.stream().filter(x -> x.getSkuId().longValue() == skuId.longValue()).findFirst();
        if(!itemVo.isPresent()){
            return ServerResponse.error(ResponseEnum.CART_IS_ERROR);
        }
        cartItemVoList.removeIf(x -> x.getSkuId().longValue() == skuId.longValue());
        if(cartItemVoList.size()==0){
            RedisUtil.del(key);
            return ServerResponse.success();
        }

        updateCart(key,cartVo);
        return ServerResponse.success();
    }

    @Override
    public ServerResponse deleteBatchCart(Long id, String skuIds) {
        if(StringUtils.isEmpty(skuIds)){
            return ServerResponse.error(ResponseEnum.CART_BATCH_DELETE_NOLL);
        }
        String key = KeyUtil.buildCartKey(id);
        String cartJson = RedisUtil.hget(key, Constants.CART_JSON_FIELD);
        CartVo cartVo = JSON.parseObject(cartJson, CartVo.class);
        List<CartItemVo> cartItemVoList = cartVo.getCartItemVoList();
        String[] skuIdArr = skuIds.split(",");

        Arrays.stream(skuIdArr).forEach(x -> cartItemVoList.removeIf(y -> y.getSkuId().longValue() == Long.parseLong(x)));
        if(cartItemVoList.size()==0){
            RedisUtil.del(key);
            return ServerResponse.success();
        }
        updateCart(key,cartVo);
        return ServerResponse.success();
    }
}
