package com.moreopen.commons.db.sharding;

import java.beans.PropertyVetoException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.moreopen.commons.db.sharding.DataSourcePool;
import com.moreopen.commons.db.sharding.DatabaseIndex;
import com.moreopen.commons.db.sharding.IndexedDataSource;

public class DataSourcePoolTest {
	
	private DataSourcePool dataSourcePool;
	
	private DatabaseIndex databaseIndex;
	
	@Before
	public void before() throws Exception {
		
	}
	
	@Test
	public void test() throws Exception {
		
		dataSourcePool = new DataSourcePool();
		dataSourcePool.setDbSize(16);
		dataSourcePool.add(createPooledDataSource(0));
		dataSourcePool.afterPropertiesSet();
		
		databaseIndex = new DatabaseIndex(2, 12);
		IndexedDataSource pool = dataSourcePool.get(databaseIndex);
		Assert.notNull(pool);
		Assert.isTrue(pool.getIndex() == 0);
		
	}
	
	@Test
	public void test0() throws Exception {
		
		dataSourcePool = new DataSourcePool();
		dataSourcePool.setDbSize(16);
		dataSourcePool.add(createPooledDataSource(0));
		dataSourcePool.add(createPooledDataSource(1));
		dataSourcePool.afterPropertiesSet();
		
		IndexedDataSource pool = dataSourcePool.get(new DatabaseIndex(2, 20));
		Assert.notNull(pool);
		Assert.isTrue(pool.getIndex() == 0);
		
		pool = dataSourcePool.get(new DatabaseIndex(7, 120));
		Assert.notNull(pool);
		Assert.isTrue(pool.getIndex() == 0);
		
		pool = dataSourcePool.get(new DatabaseIndex(8, 120));
		Assert.notNull(pool);
		Assert.isTrue(pool.getIndex() == 1);
		
		pool = dataSourcePool.get(new DatabaseIndex(14, 120));
		Assert.notNull(pool);
		Assert.isTrue(pool.getIndex() == 1);
		
	}
	
	
	private IndexedDataSource createPooledDataSource(int index) throws PropertyVetoException {
		
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setDriverClass("com.mysql.jdbc.Driver");
		dataSource.setJdbcUrl("jdbc:mysql://10.158.139.58:3306/?useUnicode=true&amp;characterEncoding=utf8&amp;autoReconnect=true");
		dataSource.setUser("root");
		dataSource.setPassword("1q2w3e4r");
		dataSource.setMinPoolSize(1);
		dataSource.setMaxPoolSize(5);
		dataSource.setInitialPoolSize(1);
		dataSource.setMaxIdleTime(600);
		dataSource.setIdleConnectionTestPeriod(120);
		dataSource.setPreferredTestQuery("select 1");
		dataSource.setTestConnectionOnCheckout(false);
		dataSource.setTestConnectionOnCheckin(false);
		return new IndexedDataSource(index, dataSource);
	}

	

}
