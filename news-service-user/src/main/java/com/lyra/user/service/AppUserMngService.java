package com.lyra.user.service;

import com.lyra.result.PageGridResult;

public interface AppUserMngService {
    public PageGridResult queryAllAppUserListByPage(String nickname, Integer status, String startDate, String endDate, Integer page, Integer pageSize);
}
