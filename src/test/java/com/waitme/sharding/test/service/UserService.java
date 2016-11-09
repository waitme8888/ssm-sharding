package com.waitme.sharding.test.service;

import com.waitme.sharding.test.bean.User;

public interface UserService {

	public User getUserById(Long id);
	
	public void updateUser(Long id);
	
}
