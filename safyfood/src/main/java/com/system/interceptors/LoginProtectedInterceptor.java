package com.system.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.system.utils.JwtHelper;
import com.system.utils.Result;
import com.system.utils.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录包含拦截器，检查请求头是否包含有效token
 *    有，有效。 放行
 *    没有，无效，返回504
 */
@Component
public class LoginProtectedInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtHelper jwtHelper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //从请求头中获取token
        String token = request.getHeader("token");

        //检查是否有效
        boolean expiration = jwtHelper.isExpiration(token);

        //有效放行
        if (!expiration){
           //放行，没有过期
           return true;
        }

        //无效返回504的状态json
        Result result = Result.build(null, ResultCodeEnum.APP_LOGIN_AUTH);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(result);
        response.getWriter().print(json);

        return false;
    }
}
