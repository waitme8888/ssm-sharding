package com.waitme.sharding.db.route;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.AbstractDataSource;

public class ReadWriteSplittingDataSource extends AbstractDataSource {
	
	private String name;
	
	private DataSource masterDataSource;
	
	private DataSource slaveDataSource;
	
	public DataSource getMasterDataSource() {
		return masterDataSource;
	}

	public void setMasterDataSource(DataSource masterDataSource) {
		this.masterDataSource = masterDataSource;
	}

	public DataSource getSlaveDataSource() {
		return slaveDataSource;
	}

	public void setSlaveDataSource(DataSource slaveDataSource) {
		this.slaveDataSource = slaveDataSource;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return determineTargetDataSource().getConnection();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return determineTargetDataSource().getConnection(username, password);
	}
	
    private DataSource determineTargetDataSource() {
        if (slaveDataSource == null) {
            return masterDataSource;
        }
//        if (this.isInTransaction()) {
//            return masterDataSource;
//        }
        
        return ReadWriteSplittingContext.isSlave() ? slaveDataSource : masterDataSource;
    }

}
