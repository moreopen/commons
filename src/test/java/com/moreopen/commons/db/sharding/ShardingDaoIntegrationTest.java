package com.moreopen.commons.db.sharding;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.KeyValue;
import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.util.Assert;

@ContextConfiguration(locations={"classpath:sharding.db.context.xml"})
public class ShardingDaoIntegrationTest extends AbstractJUnit4SpringContextTests {
	
	@Resource
	private ShardingIndexFactory shardingIndexFactory;
	
	@Resource
	private JdbcTemplates jdbcTemplates;
	
	private String sql_insert = "insert into d_test_item_%s.t_test_item_%s (wbi_resource_id, wbi_sdid) values (?,?)";
	private String sql_select = "select * from d_test_item_%s.t_test_item_%s where wbi_sdid = ?";
	private String sql_delete = "delete from d_test_item_%s.t_test_item_%s where wbi_sdid = ?";
	
	@Test
	public void test() {
		for (int i = 0; i < 1; i++) {
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

}
