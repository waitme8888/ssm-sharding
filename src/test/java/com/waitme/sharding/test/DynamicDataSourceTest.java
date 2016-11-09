package com.waitme.sharding.test;

import javax.annotation.Resource;

import org.junit.Test;

import com.waitme.sharding.test.bean.User;
import com.waitme.sharding.test.service.UserService;

public class DynamicDataSourceTest extends BaseTest{
	
	@Resource
	private UserService userService;
	
	@Test
	public void test() {
		long current = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					System.out.println("test....");
					User user = userService.getUserById(new Long(2));
					System.out.println(user.getName());
					
					
				}}).start();
			try {
				if(i%100==0) {
					
					Thread.sleep(100);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		try {
//			Thread.sleep(100000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		System.out.println("耗时：" + (System.currentTimeMillis()-current));
	}
	
	@Test
	public void testUpdate() {
		userService.updateUser(new Long(2));
	}

}
