package com.moreopen.commons.mongo;

public class MongoAccessException extends Exception {
	
	private static final long serialVersionUID = -4541205315945888349L;

	public MongoAccessException(String msg) {
		super(msg);
	}
	
	public MongoAccessException(Throwable t) {
		super(t);
	}
	
	public MongoAccessException(String msg, Throwable t) {
		super(msg, t);
	}

}
