package com.lyra.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lyra.enums.Sex;
import com.lyra.enums.UserStatus;
import com.lyra.exception.GraceException;
import com.lyra.pojo.AppUser;
import com.lyra.pojo.bo.UpdateUserBO;
import com.lyra.result.ResponseStatusEnum;
import com.lyra.user.mapper.UserMapper;
import com.lyra.user.service.AppUserService;
import com.lyra.utils.DateUtils;
import com.lyra.utils.DesensitizationUtil;
import com.lyra.utils.JsonUtil;
import com.lyra.utils.RedisOperator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class AppUserServiceImpl implements AppUserService {
   private static final String USER_FACE = "https://itcasthm-health.oss-cn-beijing.aliyuncs.com/2614344.jpg";
   @Autowired
   private UserMapper userMapper;

   @Autowired
   private RedisOperator redisOperator;

   private static final String REDIS_USER_CACHE = "redis_user_cache";


   @Override
   public AppUser queryMobileIsExist(String mobile) {
      QueryWrapper<AppUser> appUserQueryWrapper = new QueryWrapper<>();
      appUserQueryWrapper.eq("mobile", mobile);
      return userMapper.selectOne(appUserQueryWrapper);
   }

   @Override
   public AppUser createUser(String mobile) {
      AppUser appUser = new AppUser();
      appUser.setNickname("用户名:" + DesensitizationUtil.commonDisplay(mobile));
      appUser.setSex(Sex.secret.type);
      appUser.setActiveStatus(UserStatus.INACTIVE.type);
      appUser.setFace(USER_FACE);
      appUser.setBirthday(DateUtils.stringOfDate("1900-01-01"));
      appUser.setTotalIncome(0);
      appUser.setCreatedTime(new Date());
      appUser.setUpdatedTime(new Date());
      appUser.setMobile(mobile);

      userMapper.insert(appUser);

      return appUser;
   }

   @Override
   @Transactional
   public void updateUserInfo(UpdateUserBO updateUserBO) {
      // 删除redis数据
      redisOperator.del(REDIS_USER_CACHE + ":" + updateUserBO.getId());

      AppUser user = new AppUser();

      BeanUtils.copyProperties(updateUserBO, user);
      user.setActiveStatus(UserStatus.ACTIVE.type);

      int resultNum = userMapper.updateById(user);

      if (resultNum != 1) {
         GraceException.display(ResponseStatusEnum.USER_UPDATE_ERROR);
      }



      // 更新用户时 将更新后的数据查询出来并覆盖到redis中
      AppUser updateUser = this.getUser(updateUserBO.getId());
      redisOperator.set(REDIS_USER_CACHE + ":" + updateUserBO.getId(), JsonUtil.objectForJson(updateUser));

      try {
         Thread.sleep(100);
         redisOperator.del(REDIS_USER_CACHE + ":" + updateUserBO.getId());
      } catch (InterruptedException e) {
         e.printStackTrace();
      }

   }

   @Override
   public void updateUser(AppUser user) {
      userMapper.updateById(user);
   }

   @Override
   public AppUser getUser(String userId) {
      return userMapper.selectById(userId);
   }
}
