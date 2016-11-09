package com.waitme.sharding.test.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.waitme.sharding.test.bean.User;
import com.waitme.sharding.test.dao.UserMapper;

@Service("userService")
public class UserServiceImpl implements UserService {
	
	@Resource
	private UserMapper userMapper;

	@Override
	public User getUserById(Long id) {
		return userMapper.selectByPrimaryKey(id);
	}

	@Override
	public void updateUser(Long id) {
		User user = userMapper.selectByPrimaryKey(id);
		user.setName("333");
		userMapper.updateByPrimaryKeySelective(user);
		
//		throw new RuntimeException();
	}

}
