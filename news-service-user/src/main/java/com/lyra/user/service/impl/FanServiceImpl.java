package com.lyra.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyra.enums.Sex;
import com.lyra.exception.GraceException;
import com.lyra.pojo.AppUser;
import com.lyra.pojo.Fans;
import com.lyra.pojo.vo.RegionsVO;
import com.lyra.result.PageGridResult;
import com.lyra.result.ResponseStatusEnum;
import com.lyra.user.mapper.FanMapper;
import com.lyra.user.mapper.UserMapper;
import com.lyra.user.service.FanService;
import com.lyra.utils.RedisOperator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class FanServiceImpl implements FanService {
    @Autowired
    private FanMapper fanMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisOperator redisOperator;

    private static final String REDIS_MY_FOLLOW_COUNTS = "redis_my_follow_counts";
    private static final String REDIS_WRITER_FOLLOW_COUNTS = "redis_writer_follow_counts";

    private static final List<String> regions = Arrays.asList(
                        "北京", "天津", "上海", "重庆", "河北", "山西", "辽宁", "吉林", "黑龙江", "江苏", "浙江", "安徽"    , "福建", "江西", "山东",
                         "河南", "湖北", "湖南", "广东", "海南", "四川", "贵州", "云南", "陕西", "甘肃", "青海", "台湾",
                         "内蒙古", "广西", "西藏", "宁夏", "新疆", "香港", "澳门"
    );

    @Override
    public boolean queryFanCountByUserId(String writerId, String fanId) {
        QueryWrapper<Fans> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("fan_id", fanId);
        queryWrapper.eq("writer_id", writerId);

        Integer result = fanMapper.selectCount(queryWrapper);


        return result > 0;
    }

    @Override
    @Transactional
    public void addFans(String writerId, String fanId) {
        // 思路: 1. 根据用户id 将用户信息查询出来 2. 将用户信息设置到fans对象中 3. 执行插入操作 4. 修改redis 粉丝数
        AppUser appUser = userMapper.selectById(writerId);

        if (appUser == null) {
            GraceException.display(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        Fans fans = new Fans();
        fans.setWriterId(writerId);
        fans.setFanId(fanId);
        fans.setFace(appUser.getFace());
        fans.setFanNickname(appUser.getNickname());
        fans.setSex(appUser.getSex());
        fans.setProvince(appUser.getProvince());

        fanMapper.insert(fans);

        redisOperator.increment(REDIS_WRITER_FOLLOW_COUNTS + ":" + writerId, 1);
        redisOperator.increment(REDIS_MY_FOLLOW_COUNTS + ":" + fanId, 1);
    }

    @Override
    public void deleteFans(String writerId, String fanId) {
        QueryWrapper<Fans> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("writer_id", writerId);
        queryWrapper.eq("fan_id", fanId);

        fanMapper.delete(queryWrapper);

        String redisWriterFollowCounts = redisOperator.get(REDIS_WRITER_FOLLOW_COUNTS + ":" + writerId);
        String redisMyFollowCounts = redisOperator.get(REDIS_MY_FOLLOW_COUNTS + ":" + writerId);

        if (Integer.parseInt(redisWriterFollowCounts) > 0) {
            redisOperator.decrement(REDIS_WRITER_FOLLOW_COUNTS + ":" + writerId, 1);
        }

        if (Integer.parseInt(redisMyFollowCounts) > 0) {
            redisOperator.decrement(REDIS_MY_FOLLOW_COUNTS + ":" + fanId, 1);
        }

    }

    @Override
    public PageGridResult findFansListByPage(String writerId, Integer page, Integer pageSize) {
        QueryWrapper<Fans> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("writer_id", writerId);

        Page<Fans> fansPage = new Page<>(page, pageSize);

        fanMapper.selectPage(fansPage, queryWrapper);

        PageGridResult pageGridResult = new PageGridResult();
        pageGridResult.setPage(fansPage.getPages());
        pageGridResult.setRecords(fansPage.getRecords().size());
        pageGridResult.setTotal(fansPage.getTotal());
        pageGridResult.setRows(fansPage.getRecords());


        return pageGridResult;
    }

    @Override
    public Integer queryFanManCount(String writerId) {
        QueryWrapper<Fans> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("writer_id", writerId);
        queryWrapper.eq("sex", Sex.man.type);

        return fanMapper.selectCount(queryWrapper);
    }

    @Override
    public Integer queryFanWomanCount(String writerId) {
        QueryWrapper<Fans> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("writer_id", writerId);
        queryWrapper.eq("sex", Sex.woman.type);

        return fanMapper.selectCount(queryWrapper);
    }

    @Override
    public List<RegionsVO> queryRegionsCount(String writerId) {
        // 将省份遍历插入返回即可
        List<RegionsVO> regionsVOS = new ArrayList<>();
        regions.forEach((region) -> {
            QueryWrapper<Fans> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("writer_id", writerId);

            queryWrapper.eq("province", region);
            Integer regionCount = fanMapper.selectCount(queryWrapper);
            RegionsVO regionsVO = new RegionsVO();
            regionsVO.setName(region);
            regionsVO.setValue(regionCount);
            regionsVOS.add(regionsVO);
        });

        return regionsVOS;
    }
}
