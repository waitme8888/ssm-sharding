package com.waitme.sharding.db.route;

import java.lang.reflect.Method;

public interface DataSourceRoutingHandler {
	
	public String getDataSourceName(Method method, Object[] args);
}
