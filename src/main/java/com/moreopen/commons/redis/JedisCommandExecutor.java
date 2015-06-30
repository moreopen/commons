package com.moreopen.commons.redis;

import redis.clients.jedis.ShardedJedis;

public interface JedisCommandExecutor<T> {
	
	public T excute(ShardedJedis jedis, Object... argArray);
	
}
