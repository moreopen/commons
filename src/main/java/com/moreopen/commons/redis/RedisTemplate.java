package com.moreopen.commons.redis;

import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

public class RedisTemplate implements InitializingBean {

	private Logger log = Logger.getLogger("redis.error");

	private ShardedJedisPool shardedJedisPool;

	public String set(String key, String value) {
		return excuteCommand(new JedisCommandExecutor<String>() {
			public String excute(ShardedJedis shardedJedis, Object... argArray) {
				return shardedJedis.set((String) argArray[0], (String) argArray[1]);
			}
		}, key, value);
	}

	public String setex(String key, int seconds, String value) {
		return excuteCommand(new JedisCommandExecutor<String>() {
			public String excute(ShardedJedis resource, Object... argArray) {
				return resource.setex((String) argArray[0], (Integer) argArray[1], (String) argArray[2]);
			}
		}, key, seconds, value);
	}

	public String get(String key) {
		return excuteCommand(new JedisCommandExecutor<String>() {
			public String excute(ShardedJedis resource, Object... argArray) {
				return resource.get((String) argArray[0]);
			}
		}, key);
	}
	
	public long zadd(String key, double score, String value) {
		return excuteCommand(new JedisCommandExecutor<Long>() {
			@Override
			public Long excute(ShardedJedis jedis, Object... argArray) {
				return jedis.zadd((String) argArray[0], (Double) argArray[1], (String) argArray[2]);
			}
		}, key, score, value);
	}
	
	public long zremrangeByRank(String key, long start, long end) {
		return excuteCommand(new JedisCommandExecutor<Long>() {
			@Override
			public Long excute(ShardedJedis jedis, Object... argArray) {
				return jedis.zremrangeByRank((String) argArray[0], (Long) argArray[1], (Long) argArray[2]);
			}
		}, key, start, end);
	}
	
	public Set<String> zrangeByScore(String key, double min, double max) {
		return excuteCommand(new JedisCommandExecutor<Set<String>>() {
			@Override
			public Set<String> excute(ShardedJedis jedis, Object... argArray) {
				return jedis.zrangeByScore((String) argArray[0], (Double) argArray[1], (Double) argArray[2]);
			}
		}, key, min, max);
	}
	
	public long remove(String key) {
		return excuteCommand(new JedisCommandExecutor<Long>() {
			@Override
			public Long excute(ShardedJedis jedis, Object... argArray) {
				return jedis.del((String) argArray[0]);
			}
		}, key);
	}
	
	public Set<String> zrange(String key, long start, long end) {
		return excuteCommand(new JedisCommandExecutor<Set<String>>() {
			@Override
			public Set<String> excute(ShardedJedis jedis, Object... argArray) {
				return jedis.zrange((String) argArray[0], (Long) argArray[1], (Long) argArray[2]);
			}
		}, key, start, end);
	}
	
	public Long hset(String key, String field, String value) {
		return excuteCommand(new JedisCommandExecutor<Long>() {
			public Long excute(ShardedJedis shardedJedis, Object... argArray) {
				return shardedJedis.hset((String) argArray[0], (String) argArray[1], (String) argArray[2]);
			}
		}, key, field, value);
	}
	
	public String hget(String key, String field) {
		return excuteCommand(new JedisCommandExecutor<String>() {
			public String excute(ShardedJedis resource, Object... argArray) {
				return resource.hget((String) argArray[0], (String) argArray[1]);
			}
		}, key, field);
	}
	
	/**
	 * 打印出错的分片信息
	 * 
	 * @param resource
	 * @param key
	 */
	private void printWhichRedisShardError(ShardedJedis resource, Object key, Exception connEx) {
		try {
			// try to record redis node
			Jedis markJedis = resource.getShard((String) key);
			log.error(String.format(
					"Redis error, host = %s,ip = %d, key = %s, timeout = %d ",
					markJedis.getClient().getHost(), markJedis.getClient()
							.getPort(), (String) key, markJedis.getClient()
							.getTimeout()));
		} catch (Exception e) {
			// just go
		}
	}

	private <T> T excuteCommand(JedisCommandExecutor<T> executor, Object... argArray) throws JedisException {
		ShardedJedis resource = null;
		boolean isConnException = false;
		try {
			resource = shardedJedisPool.getResource();
			if (null != resource) {
				return executor.excute(resource, argArray);
			}
		} catch (JedisConnectionException connEx) {
			log.error("JedisConnectionException error", connEx);
			isConnException = true;
			printWhichRedisShardError(resource, argArray[0], connEx);
			shardedJedisPool.returnBrokenResource(resource);
			throw connEx;
		} catch (Exception e) {
			log.error("", e);
			printWhichRedisShardError(resource, argArray[0], e);
			throw new JedisConnectionException(e);
		} finally {
			try {
				if (!isConnException) {
					shardedJedisPool.returnResource(resource);
				}
			} catch (Exception e) {
				shardedJedisPool.returnBrokenResource(resource);
			}
		}
		return null;
	}

	public void setShardedJedisPool(ShardedJedisPool shardedJedisPool) {
		this.shardedJedisPool = shardedJedisPool;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		org.springframework.util.Assert.notNull(shardedJedisPool, "shardedJedisPool is required");
		
	}

}
