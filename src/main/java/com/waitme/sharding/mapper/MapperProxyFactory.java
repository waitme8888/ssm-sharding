package com.waitme.sharding.mapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.waitme.sharding.annotation.DataSourceRouting;
import com.waitme.sharding.annotation.ReadWriteSplitting;
import com.waitme.sharding.db.route.DataSourceRoutingContext;
import com.waitme.sharding.db.route.ReadWriteSplittingContext;
import com.waitme.sharding.util.StringUtils;

public class MapperProxyFactory<T> implements InvocationHandler {
	
	private Class<T> mapperInterface;
	
	private T mapper;
	
	public MapperProxyFactory() {}
	
	public MapperProxyFactory(Class<T> mapperInterface, T mapper) {
		this.mapperInterface = mapperInterface;
		this.mapper = mapper;
	}

	@SuppressWarnings("unchecked")
	public T newMapperProxy() {
		
		return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		DataSourceRouting routing = method.getDeclaringClass().getAnnotation(DataSourceRouting.class);
		if (StringUtils.isNotBlank(routing.value())) {
			DataSourceRoutingContext.set(routing.value());
		}
		ReadWriteSplitting readWriteSplitting = method.getAnnotation(ReadWriteSplitting.class);
		
		if (readWriteSplitting!=null) {
			ReadWriteSplittingContext.set(readWriteSplitting.value());
		}
		return  method.invoke(mapper, args);
	}
}
