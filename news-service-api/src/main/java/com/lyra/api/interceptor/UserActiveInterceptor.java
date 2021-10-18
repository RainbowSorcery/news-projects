package com.lyra.api.interceptor;

import com.lyra.enums.UserStatus;
import com.lyra.exception.GraceException;
import com.lyra.pojo.AppUser;
import com.lyra.result.ResponseStatusEnum;
import com.lyra.utils.JsonUtil;
import com.lyra.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserActiveInterceptor extends BaseInterceptor implements HandlerInterceptor {
    @Autowired
    protected RedisOperator redisOperator;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("headerUserId");

        String redisUserCacheJson = redisOperator.get(REDIS_USER_CACHE + ":" + userId);

        if (StringUtils.isNotBlank(redisUserCacheJson)) {
            AppUser appUser = JsonUtil.jsonToObject(redisUserCacheJson, AppUser.class);

            if (appUser.getActiveStatus() == null || appUser.getActiveStatus() != UserStatus.ACTIVE.type) {
                GraceException.display(ResponseStatusEnum.USER_INACTIVE_ERROR);
                return false;
            }
        } else {
            return false;
        }

        return true;

    }
}
