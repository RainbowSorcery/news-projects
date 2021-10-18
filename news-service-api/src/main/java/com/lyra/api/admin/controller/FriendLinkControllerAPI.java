package com.lyra.api.admin.controller;

import com.lyra.pojo.bo.FriendLinkBO;
import com.lyra.result.GraceJSONResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/friendLinkMng")
public interface FriendLinkControllerAPI {
    @PostMapping("/saveOrUpdateFriendLink")
    public GraceJSONResult saveOrUpdateFriendLink(@RequestBody @Valid FriendLinkBO friendLinkBO, BindingResult bindingResult);

    @PostMapping("/getFriendLinkList")
    public GraceJSONResult getFriendLinkList();

    @PostMapping("/delete")
    public GraceJSONResult delete(@RequestParam("linkId") String linkId);

    @GetMapping("/portal/list")
    public GraceJSONResult list();
}
