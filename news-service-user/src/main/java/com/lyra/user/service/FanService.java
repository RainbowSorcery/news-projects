package com.lyra.user.service;

import com.lyra.pojo.vo.RegionsVO;
import com.lyra.result.PageGridResult;

import java.util.List;

public interface FanService {
    public boolean queryFanCountByUserId(String writerId, String fanId);

    void addFans(String writerId, String fanId);

    void deleteFans(String writerId, String fanId);

    PageGridResult findFansListByPage(String writerId, Integer page, Integer pageSize);

    Integer queryFanManCount(String writerId);

    Integer queryFanWomanCount(String writerId);

    List<RegionsVO> queryRegionsCount(String writerId);
}
