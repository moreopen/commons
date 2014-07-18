package com.moreopen.commons.db.sharding;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * 维护所有的 datasource 信息
 * 尽量保证 dbSize 是偶数，确保每个 datasource 上的 db 是平均的，方便扩容（数据源物理机扩容）。
 * XXX 如果 dbSize 是奇数，则可以定义多个指向同一个地址的 dataSource 确保访问没有问题 
 */

public class DataSourcePool implements InitializingBean {
	
	private List<IndexedDataSource> pool = new ArrayList<IndexedDataSource>();
	
	private int dbSize;

	public IndexedDataSource get(DatabaseIndex databaseIndex) {
		int dbIndex = databaseIndex.getDbIndex();
		int dbSizePerPool = dbSize / pool.size();
		int index = dbIndex / dbSizePerPool;
		if (index >= pool.size()) {
			throw new IllegalArgumentException(String.format("invalid db index %d, pool size is only %d", index, pool.size()));
		}
		return pool.get(index);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.isTrue(dbSize > 0, "dbSize must larger than 0");
		Assert.isTrue(CollectionUtils.isNotEmpty(pool), "DataSource pool can't be empty, please check DataSource config !!!");
	}

	public void setPool(List<IndexedDataSource> pool) {
		this.pool = pool;
	}
	
	public List<IndexedDataSource> getPool() {
		return this.pool;
	}

	public void setDbSize(int dbSize) {
		this.dbSize = dbSize;
	}

	public void add(IndexedDataSource dataSource) {
		this.pool.add(dataSource);
	}
	
	public int size() {
		return pool.size();
	}
}
