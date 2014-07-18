package com.snda.youni.commons.db.sharding;

import javax.sql.DataSource;

public class IndexedDataSource {
	
	private int index;
	
	private DataSource dataSource;
	
	public IndexedDataSource(int index, DataSource dataSource) {
		this.index = index;
		this.dataSource = dataSource;
	}

	public int getIndex() {
		return index;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

}
