package com.fh.shop.api;

import com.alibaba.fastjson.JSON;
import com.fh.shop.api.member.vo.LoginVo;
import com.fh.shop.common.Constants;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class BaseController {

    public LoginVo buildMemberVo(HttpServletRequest request) {

        try {
//        LoginVo loginVo = (LoginVo) request.getAttribute(Constants.CURR_MEMBER);
            //和拦截器不是一个项目，所有不能再request中获取会员信息，在请求头中获取
            //                   解决编码问题,先在拦截器编码，再搁这里解码
            String loginVoJson = URLDecoder.decode(request.getHeader(Constants.CURR_MEMBER),"utf-8");
            return JSON.parseObject(loginVoJson, LoginVo.class);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
