package com.waitme.sharding.test.dao;

import com.waitme.sharding.annotation.DataSourceRouting;
import com.waitme.sharding.test.bean.User;

@DataSourceRouting("ssm0001")
public interface UserMapper {
	
    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
	
}