package com.waitme.sharding.db.route;

import java.lang.reflect.Method;

public class EmptyDataSourceRoutingHandler implements DataSourceRoutingHandler {

	@Override
	public String getDataSourceName(Method method, Object[] args) {
		return null;
	}

}
