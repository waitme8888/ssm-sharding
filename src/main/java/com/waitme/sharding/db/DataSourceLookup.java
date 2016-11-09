package com.waitme.sharding.db;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

public class DataSourceLookup {

	private Map<String, DataSource> dataSources = new HashMap<String, DataSource>(4);
	
	public DataSourceLookup(Map<String, DataSource> datasources) {
		this.dataSources = datasources;
	}

	public Map<String, DataSource> getDatasources() {
		return Collections.unmodifiableMap(dataSources);
	}

	public void setDatasources(Map<String, DataSource> datasources) {
		this.dataSources = datasources;
	}
	
	public DataSource getDataSource(String name) {
		return dataSources.get(name);
	}
	
	public void setDataSource(String name, DataSource dataSource) {
		dataSources.put(name, dataSource);
	}

}
