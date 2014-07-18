package com.moreopen.commons.db.sharding;

public class DatabaseIndex {
	
	private int dbIndex;
	
	private int tableIndex;

	public DatabaseIndex() {
	}
	
	public DatabaseIndex(int dbIndex, int tableIndex) {
		this.dbIndex = dbIndex;
		this.tableIndex = tableIndex;
	}

	public int getDbIndex() {
		return dbIndex;
	}

	public void setDbIndex(int dbIndex) {
		this.dbIndex = dbIndex;
	}

	public int getTableIndex() {
		return tableIndex;
	}

	public void setTableIndex(int tableIndex) {
		this.tableIndex = tableIndex;
	}
	
	public String toString() {
		return String.format("database index: db %d, table %d", dbIndex, tableIndex);
	}
	
}
