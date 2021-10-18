package com.lyra.api.interceptor;

import com.lyra.utils.IpUtil;
import com.lyra.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AlreadyReadInterceptor extends BaseInterceptor implements HandlerInterceptor {
    @Autowired
    private RedisOperator redisOperator;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 若ip+文章id存在 则拦截
        String ip = IpUtil.getIp(request);
        String articleId = request.getParameter("articleId");

        boolean result = redisOperator.keyIsExist("REDIS_ALREADY_READ" + ":" + articleId + ":" + ip);

        return !result;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
