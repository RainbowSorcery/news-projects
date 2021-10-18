package com.lyra.user.controller;

import com.lyra.api.user.controller.BaseController;
import com.lyra.api.user.controller.PassportControllerAPI;
import com.lyra.enums.UserStatus;
import com.lyra.pojo.AppUser;
import com.lyra.pojo.bo.RegisterLoginBO;
import com.lyra.result.GraceJSONResult;
import com.lyra.result.ResponseStatusEnum;
import com.lyra.user.mapper.UserMapper;
import com.lyra.user.service.AppUserService;
import com.lyra.utils.IpUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class PassportController extends BaseController implements PassportControllerAPI {
    @Autowired
    protected AppUserService appUserService;

    @Override
    public GraceJSONResult getSMSCode(@RequestParam  String mobile, HttpServletRequest request) {
        String userIp = IpUtil.getIp(request);
        redisOperator.setnx60s(MOBILE_SMS_CODE + ":" + userIp, mobile);

        String random = ((int)(Math.random() * 100000 * 8)) + "";

        // todo 发送短信

        redisOperator.set(MOBILE_SMS_CODE + ":" + mobile, random, 60 * 30);


        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult doLogin(RegisterLoginBO registerLoginBO, BindingResult bindResult, HttpServletRequest request,
                                   HttpServletResponse response) {
        // 01 首先判断字段是否有错
        if (bindResult.hasErrors()) {
            Map<String, String> bindResultErrors = getBindResultErrors(bindResult);

            return GraceJSONResult.errorMap(bindResultErrors);
        }

        // 02 判断手机号和验证码是否匹配
        String mobile = registerLoginBO.getMobile();
        String smsCode = registerLoginBO.getSmsCode();
        String redisSMSCode = redisOperator.get(MOBILE_SMS_CODE + ":" + mobile);

        if (StringUtils.isBlank(redisSMSCode) || !smsCode.equals(redisSMSCode)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }

        // 判断帐号是否存在 若不存在 创建帐号 判断是否已冻结
        AppUser appUser = appUserService.queryMobileIsExist(mobile);
        if (appUser != null && appUser.getActiveStatus().equals(UserStatus.FROZEN.type)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_FROZEN);
        } else if (appUser == null) {
            appUser = appUserService.createUser(mobile);
        }

        if (appUser.getActiveStatus() != null && !appUser.getActiveStatus().equals(UserStatus.FROZEN.type)) {
            // 将token存储至redis中
            String uToken = UUID.randomUUID().toString();
            redisOperator.set(REDIS_USER_TOKEN + ":" + appUser.getId(), uToken);

            setCookie(request, response, "utoken", uToken, COOKIE_AGE);
            setCookie(request, response, "uid", appUser.getId(), COOKIE_AGE);
        }

        redisOperator.del(MOBILE_SMS_CODE + ":" + mobile);

        return GraceJSONResult.ok(appUser.getActiveStatus());
    }

    @Override
    public GraceJSONResult logout(String userId, HttpServletRequest request, HttpServletResponse response) {

        redisOperator.del(REDIS_USER_TOKEN + ":" + userId);


        setCookie(request, response, "utoken", "", COOKIE_DELETE_TIME);
        setCookie(request, response, "uid", "", COOKIE_DELETE_TIME);

        return GraceJSONResult.ok();
    }
}
