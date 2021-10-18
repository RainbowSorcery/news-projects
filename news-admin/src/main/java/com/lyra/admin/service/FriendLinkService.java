package com.lyra.admin.service;

import com.lyra.pojo.mo.FriendLinkMO;

import java.util.List;

public interface FriendLinkService {
    public void addFriendLink(FriendLinkMO friendLinkMO);

    List<FriendLinkMO> queryAllFriendLinkList();

    void deleteFriendLinkById(String linkId);

    List<FriendLinkMO> findFriendLinkList();
}
