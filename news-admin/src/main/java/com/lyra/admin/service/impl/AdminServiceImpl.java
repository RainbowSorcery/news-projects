package com.lyra.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyra.admin.mapper.AdminMapper;
import com.lyra.admin.service.AdminService;
import com.lyra.exception.GraceException;
import com.lyra.pojo.AdminUser;
import com.lyra.pojo.bo.AdminBO;
import com.lyra.result.ResponseStatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminMapper adminMapper;



    @Override
    @Transactional
    public void addAdminUser(AdminBO adminBO) {
        AdminUser adminUser = new AdminUser();
        adminUser.setAdminName(adminBO.getAdminName());
        adminUser.setUsername(adminBO.getUsername());

        if (StringUtils.isNotBlank(adminBO.getPassword()) && StringUtils.isNotBlank(adminBO.getConfirmPassword())) {
                String password = BCrypt.hashpw(adminBO.getPassword(), BCrypt.gensalt());

            adminUser.setPassword(password);
        }

        if (StringUtils.isNotBlank(adminBO.getFaceId())) {
            adminUser.setFaceId(adminBO.getFaceId());
        }

        adminUser.setCreatedTime(new Date());
        adminUser.setUpdatedTime(new Date());

        int result = adminMapper.insert(adminUser);

        if (result != 1) {
            GraceException.display(ResponseStatusEnum.ADMIN_CREATE_ERROR);
        }

    }

    @Override
    public AdminUser findUsernameByAdmin(String username) {
        QueryWrapper<AdminUser> adminUserQueryWrapper = new QueryWrapper<>();

        adminUserQueryWrapper.eq("username", username);

        return adminMapper.selectOne(adminUserQueryWrapper);
    }

    @Override
    public IPage<AdminUser> queryUserPage(Integer page, Integer pageSize) {
        QueryWrapper<AdminUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderBy(true, true, "created_time");

        Page<AdminUser> adminUserPage = new Page<>(page, pageSize);

        adminMapper.selectPage(adminUserPage, queryWrapper);


        return adminUserPage;
    }

    @Override
    public void adminLogout(Long adminId) {

    }
}
