package com.lyra.api.interceptor;

import com.lyra.exception.GraceException;
import com.lyra.exception.MyGraceException;
import com.lyra.result.ResponseStatusEnum;
import com.lyra.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BaseInterceptor {
    protected static final String REDIS_USER_TOKEN = "redis_user_token";
    protected static final String REDIS_USER_CACHE = "redis_user_cache";

    protected static final String REDIS_ADMIN_TOKEN = "redis_admin_token";

    @Autowired
    private RedisOperator redisOperator;

    public boolean verityUserToken(String userId, String userToken, String redisPrefix) {

        if (StringUtils.isBlank(userId) && StringUtils.isBlank(userToken)) {
            GraceException.display(ResponseStatusEnum.UN_LOGIN);

            return false;
        } else {
            String redisUserToken = redisOperator.get(redisPrefix + ":" + userId);

            if (StringUtils.isBlank(redisUserToken)) {
                GraceException.display(ResponseStatusEnum.UN_LOGIN);

                return false;
            } else if (!userToken.equals(redisUserToken)) {
                GraceException.display(ResponseStatusEnum.TICKET_INVALID);
                return false;
            }
        }

        return true;
    }
}
