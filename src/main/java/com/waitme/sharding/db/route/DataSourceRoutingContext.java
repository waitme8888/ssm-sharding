package com.waitme.sharding.db.route;

public class DataSourceRoutingContext {
	
	private static final ThreadLocal<String> curDataSourceName = new ThreadLocal<String>();
	
    public static void set(String dataSourceName) {
    	curDataSourceName.set(dataSourceName);
    }
    
    public static String get() {
    	return curDataSourceName.get();
    }

}
