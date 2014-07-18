package com.snda.youni.commons.db.sharding;

public class ObjectHashedKey implements HashedKey {
	
	private Object object;
	
	public ObjectHashedKey(Object object) {
		this.object = object;
	}

	@Override
	public int getHashCode() {
		return Math.abs(object.hashCode());
	}

}
