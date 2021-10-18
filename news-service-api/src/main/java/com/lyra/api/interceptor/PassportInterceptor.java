package com.lyra.api.interceptor;

import com.lyra.api.user.controller.BaseController;
import com.lyra.exception.GraceException;
import com.lyra.result.ResponseStatusEnum;
import com.lyra.utils.IpUtil;
import com.lyra.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PassportInterceptor implements HandlerInterceptor {
    @Autowired
    protected RedisOperator redisOperator;

    protected static final String MOBILE_SMS_CODE = "mobile_sms_code";

    /**
     * 进入controller之前拦截
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userIp = IpUtil.getIp(request);

        if (redisOperator.keyIsExist(MOBILE_SMS_CODE + ":" + userIp)) {
            GraceException.display(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
            return false;
        } else {
            return true;
        }
    }
}
