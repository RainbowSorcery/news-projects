package com.lyra.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyra.pojo.AppUser;
import com.lyra.result.PageGridResult;
import com.lyra.user.mapper.UserMapper;
import com.lyra.user.service.AppUserMngService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppUserMngServiceImpl implements AppUserMngService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public PageGridResult queryAllAppUserListByPage(String nickname, Integer status, String startDate, String endDate, Integer page, Integer pageSize) {


        QueryWrapper<AppUser> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(nickname)) {
            queryWrapper.like("nickname", nickname);
        }

        if (status != null && status != 9) {
            queryWrapper.eq("active_status", status);
        }

        if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
            queryWrapper.gt("created_time", startDate);
            queryWrapper.le("   created_time", endDate);
        }

        queryWrapper.orderByAsc("created_time");

        Page<AppUser> appUserPage = new Page<>(page, pageSize);

        Page<AppUser> appUserPageList = userMapper.selectPage(appUserPage, queryWrapper);

        PageGridResult pageGridResult = new PageGridResult();
        pageGridResult.setPage(appUserPageList.getCurrent());
        pageGridResult.setRows(appUserPageList.getRecords());
        pageGridResult.setTotal(appUserPageList.getTotal());
        pageGridResult.setRecords(appUserPageList.getTotal());

        return pageGridResult;
    }
}
