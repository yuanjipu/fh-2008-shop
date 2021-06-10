package com.fh.shop.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Component
public class CrossFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletResponse response = currentContext.getResponse();
        HttpServletRequest request = currentContext.getRequest();

        //处理跨域
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,"*");
        //将自定义的请求头设置为可处理的请求头
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,"x-auth,content-type,x-token");
        //处理特殊的请求方式
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS ,"DELETE,POST,PUT,GET");


        String methodHTTP = request.getMethod();
        if (methodHTTP.equalsIgnoreCase("OPTIONS")) {
            // 禁止路由 不会继续向微服务发送请求
            currentContext.setSendZuulResponse(false);
            return null;
        }
        return null;
    }
}
