package com.fh.shop.api.cate.controller;

import com.fh.shop.api.cate.biz.ICateService;
import com.fh.shop.common.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Api(tags = "分类列表接口")
@Slf4j

public class CateController {
    @Value("${server.port}")
    private String port;
    @Resource(name = "cateService")
    private ICateService cateService;
    @GetMapping("/api/cates")
    @ApiOperation(value = "分类查询")
    public ServerResponse findList(){
        log.info("端口号为:{}",port);
        return cateService.findList();
    }
}
