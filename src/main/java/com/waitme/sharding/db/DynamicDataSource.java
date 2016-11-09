package com.waitme.sharding.db;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.AbstractDataSource;

import com.waitme.sharding.db.route.DataSourceRoutingContext;
import com.waitme.sharding.db.route.ReadWriteSplittingDataSource;
import com.waitme.sharding.util.StringUtils;

public class DynamicDataSource extends AbstractDataSource implements DataSource, BeanDefinitionRegistryPostProcessor {
	
	private static final String KEY_SEPARATOR = ".";
	
	private DataSourceLookup dataSourceLookup;
	
	private DataSource dataSourceProxy;
	
	private DataSourceFactory<? extends DataSource> dataSourceFactory;
	
	private Resource dataSourceConfigFile;
	
	private String defaultDataSource;
	
	public DynamicDataSource() {}
	
	public DynamicDataSource(DataSource dataSource) {
		dataSourceProxy = dataSource;
	}

	public DataSourceFactory<? extends DataSource> getDataSourceFactory() {
		return dataSourceFactory;
	}

	public void setDataSourceFactory(DataSourceFactory<? extends DataSource> dataSourceFactory) {
		this.dataSourceFactory = dataSourceFactory;
	}

	public Resource getDataSourceConfigFile() {
		return dataSourceConfigFile;
	}

	public void setDataSourceConfigFile(Resource dataSourceConfigFile) {
		this.dataSourceConfigFile = dataSourceConfigFile;
	}
	
	public String getDefaultDataSource() {
		return defaultDataSource;
	}

	public void setDefaultDataSource(String defaultDataSource) {
		this.defaultDataSource = defaultDataSource;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return dataSourceProxy.getConnection();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return dataSourceProxy.getConnection(username, password);
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		if (this.dataSourceLookup!=null) {
			Map<String, DataSource> dataSources = dataSourceLookup.getDatasources();
			Iterator<Entry<String, DataSource>> it = dataSources.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, DataSource> entry = it.next();
				beanFactory.registerSingleton(entry.getKey(), entry.getValue());
			}
		}
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		Properties properties = new Properties();
		try {
			properties.load(this.dataSourceConfigFile.getInputStream());
		} catch (IOException e) {
			throw new BeanInitializationException("read datasource property file error!", e);
		}
		
		try {
			this.dataSourceLookup = new DataSourceLookup(getDataSources(properties));
		} catch (SQLException e) {
			throw new BeanInitializationException("init datasource error!", e);
		}
		
		dataSourceProxy = newDataSourceProxy();
	}
	
	private DataSource newDataSourceProxy() {
		return (DataSource)Proxy.newProxyInstance(DynamicDataSource.class.getClassLoader(), new Class[]{DataSource.class}, 
		new InvocationHandler(){
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				DataSource ds = null;
				String dataSourceName = DataSourceRoutingContext.get();
				if (StringUtils.isNotBlank(dataSourceName)) {
					ds = dataSourceLookup.getDataSource(dataSourceName);
				} else {
					if (StringUtils.isBlank(defaultDataSource)) {
						ds = dataSourceLookup.getDatasources().values().iterator().next();
					} else {
						ds = dataSourceLookup.getDataSource(defaultDataSource);
					}
				}
				return method.invoke(ds, args);
			}
		});
	}
	
    private Map<String, DataSource> getDataSources(Properties properties) throws SQLException {
    	Map<String, DataSource> dataSources = new HashMap<String, DataSource>();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String[] parts = entry.getKey().toString().trim().split("\\" + KEY_SEPARATOR);
            if (parts.length == 3) {
                String name = parts[0];
            	if (StringUtils.isBlank(this.defaultDataSource)) {
            		this.defaultDataSource = name;
            	}
                if (dataSources.containsKey(name)) {
                	continue;
                }
                ReadWriteSplittingDataSource dataSource = new ReadWriteSplittingDataSource();
                dataSource.setName(name);
                for (DataSourceType dataSourceType : DataSourceType.values()) {
                	DataSourceConfig config = this.parseDataSourceConfig(name, dataSourceType, properties);
                	if (StringUtils.isNotBlank(config.getUrl())) {
                		DataSource ds = this.dataSourceFactory.getDataSource(config);
                		if (dataSourceType.equals(DataSourceType.master)) {
                			dataSource.setMasterDataSource(ds);
                		} else if (dataSourceType.equals(DataSourceType.slave)) {
                			dataSource.setSlaveDataSource(ds);
                		}
                	}
                }
                dataSources.put(name, dataSource);
            }
        }
        return dataSources;
    }
    
    private DataSourceConfig parseDataSourceConfig(String name, DataSourceType dataSourceType, Properties properties) {
        String keyPrefix = name + KEY_SEPARATOR + dataSourceType + KEY_SEPARATOR;

        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        String url = properties.getProperty(keyPrefix + "url");
//        Assert.hasText(url, keyPrefix + "url is empty!");
        dataSourceConfig.setUrl(url);

        String username = properties.getProperty(keyPrefix + "username");
//        Assert.hasText(username, keyPrefix + "username is empty!");
        dataSourceConfig.setUsername(username);

        String password = properties.getProperty(keyPrefix + "password");
//        Assert.hasText(password, keyPrefix + "password is empty!");
        dataSourceConfig.setPassword(password);

        String initialPoolSizeStr = properties.getProperty(keyPrefix + "initialPoolSize");
        int initialPoolSize = initialPoolSizeStr == null ? DataSourceConfig.DEFAULT_INI_POOL_SIZE : Integer
                .parseInt(initialPoolSizeStr);
        dataSourceConfig.setInitialPoolSize(initialPoolSize);

        String minPoolSizeStr = properties.getProperty(keyPrefix + "minPoolSize");
        int minPoolSize = minPoolSizeStr == null ? DataSourceConfig.DEFAULT_MIN_POOL_SIZE : Integer
                .parseInt(minPoolSizeStr);
        dataSourceConfig.setMinPoolSize(minPoolSize);

        String maxPoolSizeStr = properties.getProperty(keyPrefix + "maxPoolSize");
        int maxPoolSize = maxPoolSizeStr == null ? DataSourceConfig.DEFAULT_MAX_POOL_SIZE : Integer
                .parseInt(maxPoolSizeStr);
        dataSourceConfig.setMaxPoolSize(maxPoolSize);

        return dataSourceConfig;
    }
	


}
