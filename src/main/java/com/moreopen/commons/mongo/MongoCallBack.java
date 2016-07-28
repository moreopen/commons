package com.moreopen.commons.mongo;

import com.mongodb.DB;
import com.mongodb.DBCollection;

public interface MongoCallBack<T> {

	T execute(DB db, DBCollection collection);
	
}
