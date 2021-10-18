package com.lyra.api.user.controller;


import com.lyra.result.GraceJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Api(value = "controller标题", tags = {"xxx"})
@Component
public interface HelloControllerApi {

    @GetMapping("/hello")
    @ApiOperation(value = "hello方法测试", notes = "hello方法测试", httpMethod = "GET")
    public GraceJSONResult sayHello();
}
