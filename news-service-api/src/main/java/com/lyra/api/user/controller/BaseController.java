package com.lyra.api.user.controller;

import com.lyra.exception.GraceException;
import com.lyra.result.GraceJSONResult;
import com.lyra.result.ResponseStatusEnum;
import com.lyra.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseController {

    @Autowired
    protected RedisOperator redisOperator;

    @Autowired
    private UserControllerAPI userControllerAPI;

    protected static final String REDIS_USER_TOKEN = "redis_user_token";
    protected static final String MOBILE_SMS_CODE = "mobile_sms_code";
    protected static final String REDIS_USER_CACHE = "redis_user_cache";

    protected static final String REDIS_ADMIN_TOKEN = "redis_admin_token";

    protected static final String CATEGORY_CACHE = "category_cache";


    protected static final String REDIS_MY_FOLLOW_COUNTS = "redis_my_follow_counts";
    protected static final String REDIS_WRITER_FOLLOW_COUNTS = "redis_writer_follow_counts";

    protected static final Integer COOKIE_AGE = 30 * 24 * 60 * 60;
    protected static final Integer COOKIE_DELETE_TIME = 0;

    protected static final String REDIS_COMMENT_COUNT = "redis_comment_count";

    protected static final String REDIS_READ_ARTICLE_COUNT = "redis_read_article_count";

    protected void setCookie(HttpServletRequest request,
                             HttpServletResponse response,
                             String key, String value, Integer maxAge)  {
        String encoderKey = URLEncoder.encode(key, StandardCharsets.UTF_8);
        setCookieValue(request, response, encoderKey, value, maxAge);

    }

    protected void setCookieValue(HttpServletRequest request,
                                  HttpServletResponse response,
                                  String key, String value, Integer maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(maxAge);
        cookie.setDomain("lyra-heatstrings.top");
        cookie.setPath("/");

        response.addCookie(cookie);
    }


    /**
     * 循环取出BO中的错误并返回
     * @param bindResult
     * @return
     */
    protected Map<String, String> getBindResultErrors(BindingResult bindResult) {
        Map<String, String> errorsMap = new HashMap<>();

        List<FieldError> fieldErrors = bindResult.getFieldErrors();
        fieldErrors.forEach((fieldError -> {
            errorsMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        }));

        return errorsMap;
    }

    protected void deleteCookie(HttpServletRequest request,
                                HttpServletResponse response, String key) {
        setCookieValue(request, response, key, "", COOKIE_DELETE_TIME);
    }

    protected Integer getCountFromRedis(String key) {
        String count = redisOperator.get(key);

        if (StringUtils.isBlank(count)) {
            count = "0";
        }

        return Integer.parseInt(count);
    }


    protected GraceJSONResult remoteTransferGetUserInformation(String publishUserId) {
//        String queryUserInfoUrl = "http://usser-service/user/getUserInfo?userId=" + publishUserId;
//        ResponseEntity<GraceJSONResult> queryUserInfoResponseEntity = restTemplate.postForEntity(queryUserInfoUrl, null, GraceJSONResult.class);
//        if (queryUserInfoResponseEntity.getStatusCode() != HttpStatus.OK) {
//            GraceException.display(ResponseStatusEnum.ARTICLE_QUERY_PARAMS_ERROR);
//        }

//        return queryUserInfoResponseEntity.getBody();

        return userControllerAPI.getUserInfo(publishUserId);
    }
}
