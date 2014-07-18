package com.snda.youni.commons.db.sharding;

public class SpecifiedHashedKey implements HashedKey {
	
	private int value;
	
	public SpecifiedHashedKey(int value) {
		this.value = value;
	}

	@Override
	public int getHashCode() {
		return this.value;
	}

}
