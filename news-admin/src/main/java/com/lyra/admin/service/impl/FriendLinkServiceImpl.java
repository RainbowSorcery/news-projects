package com.lyra.admin.service.impl;

import com.lyra.admin.repositorie.FriendLinkMapper;
import com.lyra.admin.service.FriendLinkService;
import com.lyra.enums.YesOrNoEnums;
import com.lyra.pojo.mo.FriendLinkMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendLinkServiceImpl implements FriendLinkService {
    @Autowired
    private FriendLinkMapper friendLinkMapper
            ;

    @Override
    public void addFriendLink(FriendLinkMO friendLinkMO) {
        friendLinkMapper.save(friendLinkMO);
    }

    @Override
    public void deleteFriendLinkById(String linkId) {
        friendLinkMapper.deleteById(linkId);
    }

    @Override
    public List<FriendLinkMO> findFriendLinkList() {
        return friendLinkMapper.findAllByIsDelete(YesOrNoEnums.NO.getType());
    }

    @Override
    public List<FriendLinkMO> queryAllFriendLinkList() {
        return friendLinkMapper.findAll();
    }
}
