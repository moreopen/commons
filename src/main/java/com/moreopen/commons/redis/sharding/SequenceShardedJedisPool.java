package com.moreopen.commons.redis.sharding;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Hashing;


/**
 * sequence shared jedis pool
 */
public class SequenceShardedJedisPool extends ShardedJedisPool {
	public SequenceShardedJedisPool(
			final GenericObjectPoolConfig poolConfig,
			SequenceJedisShardInfoList sequenceJedisShardInfoList) {
		super(poolConfig, sequenceJedisShardInfoList.getJedisShardInfos(), Hashing.MURMUR_HASH);
	}
}
