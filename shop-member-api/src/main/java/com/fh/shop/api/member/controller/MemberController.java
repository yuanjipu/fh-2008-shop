package com.fh.shop.api.member.controller;

import com.alibaba.fastjson.JSON;
import com.fh.shop.api.BaseController;
import com.fh.shop.api.member.biz.IMemberService;
import com.fh.shop.api.member.vo.LoginVo;
import com.fh.shop.common.Constants;
import com.fh.shop.common.KeyUtil;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.util.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@RestController
@Api(tags = "用户接口")
public class MemberController extends BaseController {

    @Resource(name = "memberService")
    private IMemberService memberService;

    @Autowired
    private HttpServletRequest request;


    @GetMapping("/api/members/findMember")
    @ApiOperation(value = "登录用户查询")
    public ServerResponse findMember() {
        LoginVo loginVo = buildMemberVo(request);
        return ServerResponse.success(loginVo);
    }

    @GetMapping("/api/members/logout")
    @ApiOperation(value = "注销登录")
    public ServerResponse logout() throws UnsupportedEncodingException {
//        LoginVo loginVo = (LoginVo) request.getAttribute(Constants.CURR_MEMBER);
        //和拦截器不是一个项目，所有不能再request中获取会员信息，在请求头中获取
        //                   解决编码问题,先在拦截器编码，再搁这里解码
        String loginVoJson = URLDecoder.decode(request.getHeader(Constants.CURR_MEMBER),"utf-8");
        LoginVo loginVo = JSON.parseObject(loginVoJson, LoginVo.class);
        RedisUtil.del(KeyUtil.buildMemberKey(loginVo.getId()));
        return ServerResponse.success();
    }


    @PostMapping("/api/members/login")
    @ApiOperation(value = "会员登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberName" ,value = "会员名" ,paramType = "query" ,dataType = "java.lang.String"),
            @ApiImplicitParam(name = "password" ,value = "密码" ,paramType = "query" ,dataType = "java.lang.String")
    })
    public ServerResponse login(String memberName,String password){
        return memberService.login(memberName,password);
    }


}
