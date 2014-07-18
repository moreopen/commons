package com.snda.youni.commons.db.sharding;

import java.beans.PropertyVetoException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.collections.KeyValue;
import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class ShardingDaoTest {
	
	private JdbcTemplates jdbcTemplates;
	
	private ShardingIndexFactory shardingIndexFactory;
	
	private int dbSize = 2;
	
	private int tableSizePerDb = 16;
	
	private String sql_insert = "insert into d_test_item_%s.t_test_item_%s (wbi_resource_id, wbi_sdid) values (?,?)";
	private String sql_select = "select * from d_test_item_%s.t_test_item_%s where wbi_sdid = ?";
	private String sql_delete = "delete from d_test_item_%s.t_test_item_%s where wbi_sdid = ?";
	
	@Before
	public void before() throws Exception {
		jdbcTemplates = new JdbcTemplates();
		DataSourcePool dataSourcePool = new DataSourcePool();
		dataSourcePool.add(createPooledDataSource(0));
		dataSourcePool.setDbSize(dbSize);
//		dataSourcePool.add(createPooledDataSource1(1));
		dataSourcePool.afterPropertiesSet();
		
		jdbcTemplates.setDataSourcePool(dataSourcePool);
		jdbcTemplates.afterPropertiesSet();
		
		shardingIndexFactory = new ShardingIndexFactory(dbSize, tableSizePerDb);
		shardingIndexFactory.afterPropertiesSet();
	}
	
	@Test
	public void test() {
		for (int i = 0; i < 100; i++) {
			String resourceId = "rid-0000000" + i;
			long sdid = 13818088962l + i;
			
//			String md5Hex = MD5Utils.md5Hex(sdid + "");
			DatabaseIndex index = shardingIndexFactory.getIndex(new ObjectHashedKey(sdid + ""));
			System.out.println(String.format("========get database index, db:%s, table:%s", index.getDbIndex(), index.getTableIndex()));
			JdbcTemplate jdbcTemplate = jdbcTemplates.get(index);
			
			//insert
			jdbcTemplate.update(String.format(sql_insert, index.getDbIndex(), index.getTableIndex()), resourceId, sdid);
			
			//query, XXX record hit ratio to check after do expansion  
			List<KeyValue> result = jdbcTemplate.query(String.format(sql_select, index.getDbIndex(), index.getTableIndex()), new Object[] {sdid}, new RowMapper<KeyValue>() {
				@Override
				public KeyValue mapRow(ResultSet rs, int rowNum) throws SQLException {
					return new DefaultKeyValue(rs.getObject("wbi_resource_id"), rs.getObject("wbi_sdid"));
				}
			});
			Assert.isTrue(result.size() == 1 && result.get(0).getKey().equals(resourceId) && Long.valueOf(result.get(0).getValue() + "") == sdid);
			
			//delete
			int cnt = jdbcTemplate.update(String.format(sql_delete, index.getDbIndex(), index.getTableIndex()), sdid);
			Assert.isTrue(cnt == 1);
			
			//ensure delete success
			List<KeyValue> result0 = jdbcTemplate.query(String.format(sql_select, index.getDbIndex(), index.getTableIndex()), new Object[] {sdid}, new RowMapper<KeyValue>() {
				@Override
				public KeyValue mapRow(ResultSet rs, int rowNum) throws SQLException {
					return new DefaultKeyValue(rs.getObject("wbi_resource_id"), rs.getObject("wbi_sdid"));
				}
			});
			Assert.isTrue(result0.isEmpty());
		}
	}
	
	private IndexedDataSource createPooledDataSource(int index) throws PropertyVetoException {
		
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setDriverClass("com.mysql.jdbc.Driver");
		dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/?useUnicode=true&amp;characterEncoding=utf8&amp;autoReconnect=true");
		dataSource.setUser("root");
		dataSource.setPassword("root");
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
	
	private IndexedDataSource createPooledDataSource1(int index) throws PropertyVetoException {
		
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setDriverClass("com.mysql.jdbc.Driver");
		dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/?useUnicode=true&amp;characterEncoding=utf8&amp;autoReconnect=true");
		dataSource.setUser("root");
		dataSource.setPassword("root");
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
