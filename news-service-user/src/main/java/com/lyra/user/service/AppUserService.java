package com.lyra.user.service;

import com.lyra.pojo.AppUser;
import com.lyra.pojo.bo.UpdateUserBO;

public interface AppUserService {
    /**
     * 查询帐号是否存在 若存在返回用户信息
     * @param mobile 手机号
     * @return 用户信息
     */
    AppUser queryMobileIsExist(String mobile);

    /**
     * 添加用户
     * @param mobile 手机号
     * @return 用户信息
     */
    AppUser createUser(String mobile);

    /**
     * 根据用户id获取用户
     * @param userId 用户id
     * @return 用户
     */
    AppUser getUser(String userId);

    void updateUserInfo(UpdateUserBO updateUserBO);

    void updateUser(AppUser user);
}
