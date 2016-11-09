package com.waitme.sharding.mapper;

import org.springframework.beans.factory.FactoryBean;

public class MapperFactoryBean<T> extends org.mybatis.spring.mapper.MapperFactoryBean<T> implements FactoryBean<T> {

	@Override
	public T getObject() throws Exception {
		final T mapper = super.getObject();
		return new MapperProxyFactory<T>(getMapperInterface(), mapper).newMapperProxy();
	}
	

}
