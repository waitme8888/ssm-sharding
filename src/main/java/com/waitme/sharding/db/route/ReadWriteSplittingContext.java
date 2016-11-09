package com.waitme.sharding.db.route;

import com.waitme.sharding.db.DataSourceType;

public class ReadWriteSplittingContext {

    private static final ThreadLocal<DataSourceType> curDataSourceType = new ThreadLocal<DataSourceType>();

    public static void set(DataSourceType dataSourceType) {
        curDataSourceType.set(dataSourceType);
    }

    public static void setMaster() {
        curDataSourceType.set(DataSourceType.master);
    }

    public static void clear() {
        curDataSourceType.remove();
    }

    public static boolean isMaster() {
        return DataSourceType.master == curDataSourceType.get();
    }
    
    public static boolean isSlave() {
    	return DataSourceType.slave == curDataSourceType.get();
    }

}
