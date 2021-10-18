package com.lyra.user.controller;

import com.lyra.api.user.controller.HelloControllerApi;
import com.lyra.result.GraceJSONResult;
import com.lyra.result.ResponseStatusEnum;
import com.lyra.utils.RedisOperator;
import com.lyra.utils.extend.AliyunResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HelloController implements HelloControllerApi {
    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @Autowired
    private AliyunResources aliyunResources;

    @Autowired
    private RedisOperator redisOperator;



    public GraceJSONResult sayHello() {
        redisOperator.set("Hello", "world");
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_FACE_NULL_ERROR);
    }
}
