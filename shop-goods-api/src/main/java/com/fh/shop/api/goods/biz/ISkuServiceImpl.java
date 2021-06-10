package com.fh.shop.api.goods.biz;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.internal.org.apache.commons.lang3.StringUtils;
import com.fh.shop.api.goods.mapper.ISkuMapper;
import com.fh.shop.api.goods.po.Sku;
import com.fh.shop.api.goods.vo.SkuVo;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.util.RedisUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
@Transactional(rollbackFor = Exception.class)
@Service("skuService")
public class ISkuServiceImpl implements ISkuService {

    @Resource
    private ISkuMapper skuMapper;
    @Override
    @Transactional(readOnly = true)
    public ServerResponse list() {
//        从redis取值
        String skuListInfo = RedisUtil.get("skuList");
        List<Sku> skuList = skuMapper.findRecommendNewProductList();
        if(StringUtils.isNotEmpty(skuListInfo)){
//            不为空直接转换之后返回到前台
            List<SkuVo> skuVoList = JSON.parseArray(skuListInfo, SkuVo.class);
            return ServerResponse.success(skuVoList);
        }
//        为空从数据库查询
        List<SkuVo> skuVoList = skuList.stream().map(x -> {
            SkuVo skuVo = new SkuVo();
            skuVo.setId(x.getId());
            skuVo.setImage(x.getColorImage());
            skuVo.setPrice(x.getPrice().toString());
            skuVo.setSkuName(x.getSkuName());
            return skuVo;

        }).collect(Collectors.toList());
//        转换成json字符串放到redis顺便返回到前台
        String skuListJson = JSON.toJSONString(skuVoList);
        RedisUtil.setex("skuList",skuListJson,30);
        return ServerResponse.success(skuVoList);
    }

    @Override
    public ServerResponse findSku(Long id) {
        Sku sku = skuMapper.selectById(id);
        return ServerResponse.success(sku);
    }
}
