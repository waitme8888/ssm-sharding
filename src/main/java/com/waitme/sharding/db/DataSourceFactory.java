package com.waitme.sharding.db;

import java.sql.SQLException;

import javax.sql.DataSource;

public interface DataSourceFactory<T extends DataSource> {

    T getDataSource(DataSourceConfig config) throws SQLException;

}