package com.fh.shop.api.goods.controller;

import com.fh.shop.api.goods.biz.ISkuService;
import com.fh.shop.common.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.ws.rs.QueryParam;

@RestController
@Api(tags = "商品接口")
public class SkuController {

    @Resource(name = "skuService")
    private ISkuService skuService;

    @GetMapping("/api/skus/recommend/mewprodect")
    @ApiOperation(value = "展示商品查询")
    public ServerResponse list(){
        return skuService.list();
    }

    @ApiOperation(value = "购物车用的商品查询")
    @GetMapping("/api/skus/findSku")
    public ServerResponse findSku(@RequestParam("id") Long id){
        return skuService.findSku(id);
    }
}
