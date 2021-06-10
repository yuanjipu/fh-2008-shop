package com.fh.shop.api.cart.controller;

import com.fh.shop.api.BaseController;
import com.fh.shop.api.cart.biz.ICartService;
import com.fh.shop.api.member.vo.LoginVo;
import com.fh.shop.common.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RequestMapping("/api/carts")
@RestController
@Api(tags = "购物车接口")
public class CartController extends BaseController {

    @Resource(name = "cartService")
    private ICartService cartService;
    @Resource
    private HttpServletRequest request;

    @PostMapping("/addCartItem")
    @ApiOperation(value = "购物车添加商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "skuId" ,value = "商品ID" ,dataType = "java.lang.Long" ,required = true),
            @ApiImplicitParam(name = "count" ,value = "添加数量" ,dataType = "java.lang.Long" ,required = true),
            @ApiImplicitParam(name = "x-auth" ,value = "头信息" ,dataType = "java.lang.String" ,required = true ,paramType = "header")
    })
    public ServerResponse addCartItem(Long skuId ,Long count){

        LoginVo loginVo = buildMemberVo(request);
        Long memberId = loginVo.getId();
        return cartService.addItem(memberId,skuId,count);
    }

    @GetMapping("/findCart")
    @ApiOperation(value = "查询购物车")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth" ,value = "头信息" ,dataType = "java.lang.String" ,required = true ,paramType = "header")
    })
    public ServerResponse findCart(){
        LoginVo loginVo = buildMemberVo(request);
        return cartService.findCart(loginVo.getId());
    }

    @GetMapping("/findCartCount")
    @ApiOperation(value = "查询购物车商品个数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth" ,value = "头信息" ,dataType = "java.lang.String" ,required = true ,paramType = "header")
    })
    public ServerResponse findCartCount(){
        LoginVo loginVo = buildMemberVo(request);
        return cartService.findCartCount(loginVo.getId());
    }

    @DeleteMapping("/deleteCart")
    @ApiOperation(value = "删除购物车商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth" ,value = "头信息" ,dataType = "java.lang.String" ,required = true ,paramType = "header")
    })
    public ServerResponse deleteCart(Long skuId){
        LoginVo loginVo = buildMemberVo(request);
        return cartService.deleteCart(loginVo.getId(),skuId);
    }

    @DeleteMapping("/deleteBatchCart")
    @ApiOperation(value = "批量删除购物车商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x-auth" ,value = "头信息" ,dataType = "java.lang.String" ,required = true ,paramType = "header")
    })
    public ServerResponse deleteBatchCart(String skuIds){
        LoginVo loginVo = buildMemberVo(request);
        return cartService.deleteBatchCart(loginVo.getId(),skuIds);
    }
}
