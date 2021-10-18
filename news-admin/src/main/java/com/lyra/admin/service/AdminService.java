package com.lyra.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lyra.pojo.AdminUser;
import com.lyra.pojo.bo.AdminBO;

public interface AdminService {
    public AdminUser findUsernameByAdmin(String username);

    void addAdminUser(AdminBO adminBO);

    IPage<AdminUser> queryUserPage(Integer page, Integer pageSize);

    void adminLogout(Long adminId);
}
