package com.moreopen.commons.mongo;

import com.mongodb.DB;
import com.mongodb.DBCollection;

public class MongoTemplate {
	
	private DBManager dbManager;
	
	public <T> T execute(String dbName, String collectionName, MongoCallBack<T> callback) throws MongoAccessException {
		DB db = dbManager.getDB(dbName);
		if (db == null) {
			throw new MongoAccessException(String.format("can't find db [%s]", dbName));
		}
		try {
			db.requestStart();
			DBCollection areaResource = db.getCollection(collectionName);
			if (areaResource == null) {
				throw new MongoAccessException(String.format("can't find collection [%s]", collectionName));
			}
			return callback.execute(db, areaResource);
		} catch (Exception e) {
			throw new MongoAccessException(e);
		} finally {
			db.requestDone();
		}
	}

	public void setDbManager(DBManager dbManager) {
		this.dbManager = dbManager;
	}

}
