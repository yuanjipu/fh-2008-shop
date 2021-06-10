package com.fh.shop.filter;

import com.alibaba.fastjson.JSON;
import com.fh.shop.common.Constants;
import com.fh.shop.common.KeyUtil;
import com.fh.shop.common.ResponseEnum;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.util.Md5Util;
import com.fh.shop.util.RedisUtil;
import com.fh.shop.vo.LoginVo;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.List;
@Slf4j
@Component
public class JwtFilter extends ZuulFilter {
    @Value("${fh.shop.member.checkUrls}")
    private List<String> checkUrls;
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @SneakyThrows
    @Override
    public Object run() throws ZuulException {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        StringBuffer requestURL = request.getRequestURL();
        log.info("==========={}",checkUrls);
        boolean isCheck = true;
        for (String checkUrl : checkUrls) {
            if(requestURL.indexOf(checkUrl)>0){
                isCheck = false;
                break;
            }
        }
        if(isCheck){
            //直接放行
            return null;
        }

        //请求验证
        //通过请求头传过来的用户信息的json字符串和签名
        //先判断请求头是否为空
        String header = request.getHeader("x-auth");
        if(StringUtils.isEmpty(header)){
            return buildResponse(ResponseEnum.TOKEN_IS_MISS);
        }
        //判断头信息是否格式正确
        String[] headerArr = header.split("\\.");
        if(headerArr.length!=2){
            return buildResponse(ResponseEnum.TOKEN_IS_NOT_FELL);
        }
        //取出用户信息和签名进行散列判断
        String memberVoJsonBase64 = headerArr[0];
        String signBase64 = headerArr[1];
        String memberVoJson = new String(Base64.getDecoder().decode(memberVoJsonBase64),"utf-8");
        String sign = new String(Base64.getDecoder().decode(signBase64),"utf-8");
        String newSign = Md5Util.sign(memberVoJson, Constants.SECRET);
        if(!newSign.equals(sign)){
            return buildResponse(ResponseEnum.TOKEN_IS_MISS);
        }

        //验证redis缓存中jwt是否过期
        LoginVo loginVo = JSON.parseObject(memberVoJson, LoginVo.class);
        Long id = loginVo.getId();
        if(!RedisUtil.exists(KeyUtil.buildMemberKey(id))){
            return buildResponse(ResponseEnum.MEMBER_IS_LOGIN_ERROR);
        }
        //将登陆的会员信息放到request中，方面在处理请求的时候获取
        //request.setAttribute(Constants.CURR_MEMBER,loginVo);
        //微服务拦截器和member不是一个项目，所有不是一个request，在这里放到request中在member项目获取不到
        //放到请求头中，在member项目中在请求头中获取
        //                                                         解决中文乱码，先将其进行编码
        currentContext.addZuulRequestHeader(Constants.CURR_MEMBER,URLEncoder.encode(memberVoJson,"utf-8"));

        //续命
        RedisUtil.expire(KeyUtil.buildMemberKey(id),Constants.TOKEN_EXPIRE);

        return null;
    }

    private Object buildResponse(ResponseEnum responseEnum) {
        RequestContext currentContext = RequestContext.getCurrentContext();

        HttpServletResponse response = currentContext.getResponse();
        //解决中文乱码
        response.setContentType("application/json;charset=utf-8");
        currentContext.setSendZuulResponse(false);//拦截请求，不在调用微服务

        ServerResponse error = ServerResponse.error(responseEnum);
        String errorJson = JSON.toJSONString(error);
        currentContext.setResponseBody(errorJson);//给客户端的提示信息，必须是字符串
        return null;
    }
}
