package com.lyra.api.user.controller;

import com.lyra.api.config.FeignConfig;
import com.lyra.pojo.bo.UpdateUserBO;
import com.lyra.result.GraceJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Component
@Api(value = "用户操作接口", tags = "用户操作controller")
@FeignClient(value = "user-service", path = "/user", configuration = FeignConfig.class)
public interface UserControllerAPI {
    @ApiOperation(value = "获取用户基本信息", notes = "获取用户基本信息", httpMethod = "GET")
    @PostMapping("/getUserInfo")
    public GraceJSONResult getUserInfo(@RequestParam("userId") String userId);

    @ApiOperation(value = "获取用户信息", notes = "获取用户信息", httpMethod = "POST")
    @PostMapping("/getAccountInfo")
    public GraceJSONResult getAccountInfo(@RequestParam String userId);

    @ApiOperation(value = "用户信息更新", notes = "用户信息更新", httpMethod = "POST")
    @PostMapping("/updateUserInfo")
    public GraceJSONResult updateUserInfo(@RequestBody @Valid UpdateUserBO updateUserBO);

    @GetMapping("/queryUserListByUserIds")
    public GraceJSONResult queryUserListByUserIds(@RequestParam String userIds);


}
