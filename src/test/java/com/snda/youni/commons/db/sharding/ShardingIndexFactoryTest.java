package com.snda.youni.commons.db.sharding;

import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

public class ShardingIndexFactoryTest {

	private ShardingIndexFactory indexFactory;
	
	private HashedKey hashedKey;
	
	@Before
	public void before() {
		indexFactory = new ShardingIndexFactory();
	}
	
	@Test
	public void test() {
		hashedKey = new ObjectHashedKey("reetedfde");
		DatabaseIndex databaseIndex = indexFactory.getIndex(hashedKey);
		System.out.println(databaseIndex);
	}
	
	@Test
	public void test0() {
		hashedKey = new SpecifiedHashedKey(5);
		DatabaseIndex databaseIndex = indexFactory.getIndex(hashedKey);
		System.out.println(databaseIndex);
		Assert.isTrue(databaseIndex.getDbIndex() == 0);
		Assert.isTrue(databaseIndex.getTableIndex() == 0);
	}
	
	@Test
	public void test1() {
		indexFactory.setDbSize(2);
		indexFactory.setTableSizePerDb(8);
		
		DatabaseIndex databaseIndex = indexFactory.getIndex(new SpecifiedHashedKey(5));
		Assert.isTrue(databaseIndex.getDbIndex() == 0);
		Assert.isTrue(databaseIndex.getTableIndex() == 5);
		
		databaseIndex = indexFactory.getIndex(new SpecifiedHashedKey(8));
		Assert.isTrue(databaseIndex.getDbIndex() == 1);
		Assert.isTrue(databaseIndex.getTableIndex() == 8);
		
		databaseIndex = indexFactory.getIndex(new SpecifiedHashedKey(11));
		Assert.isTrue(databaseIndex.getDbIndex() == 1);
		Assert.isTrue(databaseIndex.getTableIndex() == 11);
		
		databaseIndex = indexFactory.getIndex(new SpecifiedHashedKey(31));
		Assert.isTrue(databaseIndex.getDbIndex() == 1);
		Assert.isTrue(databaseIndex.getTableIndex() == 15);
		
		indexFactory.setDbSize(16);
		indexFactory.setTableSizePerDb(16);
		databaseIndex = indexFactory.getIndex(new SpecifiedHashedKey(31));
		Assert.isTrue(databaseIndex.getDbIndex() == 1);
		Assert.isTrue(databaseIndex.getTableIndex() == 31);
		
		databaseIndex = indexFactory.getIndex(new SpecifiedHashedKey(133));
		Assert.isTrue(databaseIndex.getDbIndex() == 8);
		Assert.isTrue(databaseIndex.getTableIndex() == 133);
		
		databaseIndex = indexFactory.getIndex(new SpecifiedHashedKey(1330));
		Assert.isTrue(databaseIndex.getDbIndex() == 3);
		Assert.isTrue(databaseIndex.getTableIndex() == 50);
		
	}

}
