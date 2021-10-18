package com.lyra.user.controller;

import com.lyra.api.user.controller.AppUserControllerAPI;
import com.lyra.api.user.controller.BaseController;
import com.lyra.exception.GraceException;
import com.lyra.pojo.AppUser;
import com.lyra.result.GraceJSONResult;
import com.lyra.result.PageGridResult;
import com.lyra.result.ResponseStatusEnum;
import com.lyra.user.service.AppUserMngService;
import com.lyra.user.service.AppUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppUserController extends BaseController implements AppUserControllerAPI {
    @Autowired
    private AppUserMngService appUserMngService;

    @Autowired
    private AppUserService appUserService;

    @Override
    public GraceJSONResult queryAll(String nickname, Integer status, String startDate, String endDate, Integer page, Integer pageSize) {
        if (page == null) {
            page = 0;
        }

        if (pageSize == null) {
            pageSize = 10;
        }

        PageGridResult pageGridResult = appUserMngService.queryAllAppUserListByPage(nickname, status, startDate, endDate, page, pageSize);

        return GraceJSONResult.ok(pageGridResult);
    }

    @Override
    public GraceJSONResult freezeUserOrNot(String userId, Integer doStatus) {
        if (StringUtils.isBlank(userId)) {
            GraceException.display(ResponseStatusEnum.FAILED);
        }

        if (doStatus == null) {
            GraceException.display(ResponseStatusEnum.FAILED);
        }

        AppUser user = appUserService.getUser(userId);
        if (user == null) {
            GraceException.display(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        user.setActiveStatus(doStatus);

        appUserService.updateUser(user);

        // 修改状态后 清空session 当用户进行操作时 需要重新登录
        redisOperator.del(REDIS_USER_CACHE + ":" + userId);
        redisOperator.del(REDIS_USER_CACHE + ":" + userId);

        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult userDetail(String userId) {
        if (StringUtils.isBlank(userId)) {
            GraceException.display(ResponseStatusEnum.FAILED);
        }

        AppUser user = appUserService.getUser(userId);

        return GraceJSONResult.ok(user);
    }
}
