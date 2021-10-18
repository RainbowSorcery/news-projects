package com.lyra.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.lyra.api.user.controller.BaseController;
import com.lyra.api.user.controller.UserControllerAPI;
import com.lyra.exception.GraceException;
import com.lyra.pojo.AppUser;
import com.lyra.pojo.bo.UpdateUserBO;
import com.lyra.pojo.vo.AccountBasicInfoVO;
import com.lyra.pojo.vo.AppUserVO;
import com.lyra.result.GraceJSONResult;
import com.lyra.result.ResponseStatusEnum;
import com.lyra.user.service.AppUserService;
import com.lyra.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping( "/user")
public class UserController extends BaseController implements UserControllerAPI {
    private Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    protected AppUserService userService;

    @Autowired
    private ObjectMapper objectMapper;


    @Value("${server.port}")
    private String port;

    @Override
    public GraceJSONResult getAccountInfo(String userId) {
        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.UN_LOGIN);
        }
        AppUser appUser = getUser(userId);

        AppUserVO appUserVO = new AppUserVO();

        BeanUtils.copyProperties(appUser, appUserVO);

        return GraceJSONResult.ok(appUserVO);
    }

    @Override
    public GraceJSONResult updateUserInfo(UpdateUserBO updateUserBO) {
//        if (bindingResult.hasErrors()) {
//            Map<String, String> bindResultErrors = getBindResultErrors(bindingResult);
//
//            return GraceJSONResult.errorMap(bindResultErrors);
//        }



        userService.updateUserInfo(updateUserBO);



        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult queryUserListByUserIds(String userIds) {
        if (StringUtils.isBlank(userIds)) {
            GraceException.display(ResponseStatusEnum.USER_STATUS_ERROR);
        }

        List<String> userIdList = null;
        try {
            userIdList = objectMapper.readValue(userIds, TypeFactory.defaultInstance().constructCollectionType(List.class, String.class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        if (userIdList == null || userIdList.size() <= 0) {
            GraceException.display(ResponseStatusEnum.USER_STATUS_ERROR);
        }

        List<AccountBasicInfoVO> appUsers = new ArrayList<>();

        userIdList.forEach((userId) -> {
            // 因为首页会被用户不停的刷新 可以将用户数据存储到redis中以减少数据库的压力
            AppUser user = getUser(userId);
            AccountBasicInfoVO accountBasicInfoVO =
                    new AccountBasicInfoVO();

            BeanUtils.copyProperties(user, accountBasicInfoVO);
            appUsers.add(accountBasicInfoVO);

        });

        return GraceJSONResult.ok(appUsers);
    }

    @Override
    public GraceJSONResult getUserInfo(String userId) {

        if (StringUtils.isBlank(userId)) {
            return new GraceJSONResult(ResponseStatusEnum.UN_LOGIN);
        }

        AccountBasicInfoVO accountBasicInfoVO =
                new AccountBasicInfoVO();

        AppUser user = getUser(userId);

        BeanUtils.copyProperties(user, accountBasicInfoVO);

        // 获取粉丝数和关注数 将粉丝数和关注数设置到用户基本信息中 查询用户时从redis中获取粉丝数和关注数即可
        Integer redisWriterFollowCounts = getCountFromRedis(REDIS_WRITER_FOLLOW_COUNTS + ":" + userId);
        Integer redisMyFollowCounts = getCountFromRedis(REDIS_MY_FOLLOW_COUNTS + ":" + userId);

        accountBasicInfoVO.setMyFansCounts(redisMyFollowCounts);
        accountBasicInfoVO.setMyFollowCounts(redisWriterFollowCounts);

        return GraceJSONResult.ok(accountBasicInfoVO);
    }



    private AppUser getUser(String userId) {
        String redisUser = redisOperator.get(REDIS_USER_CACHE + ":" + userId);

        AppUser appUser = null;

        if (StringUtils.isBlank(redisUser)) {
            appUser = userService.getUser(userId);
            String appUserJson = JsonUtil.objectForJson(appUser);
            redisOperator.set(REDIS_USER_CACHE + ":" + userId, appUserJson);
        } else {
            appUser = JsonUtil.jsonToObject(redisUser, AppUser.class);
        }

        return appUser;
    }
}
