package com.moreopen.commons.db.sharding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * 根据 {@see HashedKey} 定位对应的库、表位置信息
 */
public class ShardingIndexFactory implements InitializingBean {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * db 的数量, default is 1
	 */
	private int dbSize = 1;
	
	/**
	 * 每个 db 表的数量, default is 1
	 */
	private int tableSizePerDb = 1;
	
	public ShardingIndexFactory() {
	}
	
	public ShardingIndexFactory(int dbSize, int tableSizePerDb) {
		this.dbSize = dbSize;
		this.tableSizePerDb = tableSizePerDb;
	}

	public DatabaseIndex getIndex(HashedKey key) {
		
		int tableSize = dbSize * tableSizePerDb;
		int hashCode = key.getHashCode();
		Assert.isTrue(hashCode >= 0);
		int tableIndex = hashCode % tableSize;
		int dbIndex = tableIndex / tableSizePerDb;
		return new DatabaseIndex(dbIndex, tableIndex);
	}

	public int getDbSize() {
		return dbSize;
	}

	public void setDbSize(int dbSize) {
		this.dbSize = dbSize;
	}

	public int getTableSizePerDb() {
		return tableSizePerDb;
	}

	public void setTableSizePerDb(int tableSizePerDb) {
		this.tableSizePerDb = tableSizePerDb;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (dbSize/2 * 2 != dbSize) {
			//dbSize is not even number
			logger.warn("=========== dbSize is not even number, are you sure !!??");
		}
	}

}
