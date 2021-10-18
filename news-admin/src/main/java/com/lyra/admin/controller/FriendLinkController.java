package com.lyra.admin.controller;

import com.lyra.admin.service.FriendLinkService;
import com.lyra.api.admin.controller.FriendLinkControllerAPI;
import com.lyra.api.user.controller.BaseController;
import com.lyra.pojo.bo.FriendLinkBO;
import com.lyra.pojo.mo.FriendLinkMO;
import com.lyra.result.GraceJSONResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
public class FriendLinkController extends BaseController implements FriendLinkControllerAPI {
    @Autowired
    private FriendLinkService friendLinkService;

    @Override
    public GraceJSONResult delete(String linkId) {
        friendLinkService.deleteFriendLinkById(linkId);

        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult list() {
        List<FriendLinkMO> friendLinkList = friendLinkService.findFriendLinkList();

        return GraceJSONResult.ok(friendLinkList);
    }

    @Override
    public GraceJSONResult getFriendLinkList() {
        List<FriendLinkMO> friendLinkMOS = friendLinkService.queryAllFriendLinkList();

        return GraceJSONResult.ok(friendLinkMOS);
    }

    @Override
    public GraceJSONResult saveOrUpdateFriendLink(FriendLinkBO friendLinkBO, BindingResult bindingResult) {
        // 0. 校验参数是否正确
        if (bindingResult.hasErrors()) {
            Map<String, String> bindResultErrors = super.getBindResultErrors(bindingResult);

            GraceJSONResult.errorMap(bindResultErrors);
        }

        // 1. 将BO对象转换为MO对象
        FriendLinkMO friendLinkMO = new FriendLinkMO();
        BeanUtils.copyProperties(friendLinkBO, friendLinkMO);
        friendLinkMO.setCreateTime(new Date());
        friendLinkMO.setUpdateTime(new Date());

        friendLinkService.addFriendLink(friendLinkMO);

        return GraceJSONResult.ok();
    }


}
