package com.lyra.api.user.controller;

import com.lyra.pojo.bo.RegisterLoginBO;
import com.lyra.result.GraceJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Api(value = "登录注册api", tags = "用户登录注册controller")
@Component
@RequestMapping("/passport")
public interface PassportControllerAPI {
    @ApiOperation(value = "获取验证码", notes = "获取短信验证码", httpMethod = "GET")
    @GetMapping("/getSMSCode")
    public GraceJSONResult getSMSCode(String mobile, HttpServletRequest request);

    @ApiOperation(value = "用户登录或注册验证", notes = "用户登录或注册验证", httpMethod = "POST")
    @PostMapping("/doLogin")
    public GraceJSONResult doLogin(@Valid @RequestBody RegisterLoginBO registerLoginBO,
                                   BindingResult bindingResult,
                                   HttpServletRequest request,
                                   HttpServletResponse response);


    @ApiOperation(value = "用户退出登录", notes = "用户退出登录", httpMethod = "POST")
    @PostMapping("/logout")
    public GraceJSONResult logout(@RequestParam String userId,
                                  HttpServletRequest request,
                                  HttpServletResponse response);
}
