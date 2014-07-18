package com.snda.youni.commons.db.sharding;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

public class JdbcTemplates implements InitializingBean {
	
	private List<JdbcTemplate> jdbcTemplates = new ArrayList<JdbcTemplate>();
	
	private DataSourcePool dataSourcePool;

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.isTrue(dataSourcePool.size() > 0, "dataSource pool cant be empty");
	
		List<IndexedDataSource> pool = dataSourcePool.getPool();
		for (IndexedDataSource indexedDataSource : pool) {
			JdbcTemplate template = new JdbcTemplate(indexedDataSource.getDataSource());
			jdbcTemplates.add(indexedDataSource.getIndex(), template);
		}
	}

	public void setDataSourcePool(DataSourcePool dataSourcePool) {
		this.dataSourcePool = dataSourcePool;
	}

	public JdbcTemplate get(DatabaseIndex index) {
		IndexedDataSource indexedDataSource = dataSourcePool.get(index);
		return jdbcTemplates.get(indexedDataSource.getIndex());
	}
	
}
