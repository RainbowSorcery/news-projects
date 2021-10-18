package com.lyra.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lyra.pojo.AppUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<AppUser> {
}
