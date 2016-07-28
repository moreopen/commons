package com.moreopen.commons.redis;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

public class JedisSentinelTest {
	
	@Test
	public void test() throws InterruptedException {
		for (int i = 0; i < 100; i++) {
			try {
				Set<String> sentinels = new HashSet<String>();
				sentinels.add("116.211.28.174:26379");
				JedisSentinelPool pool = new JedisSentinelPool("mymaster", sentinels);
				Jedis jedis = pool.getResource();
				String r = jedis.set("testname", "yk" + i);
				System.out.println("set return : " + r);
				String value = jedis.get("testname");
				System.out.println("get value : " + value);
				Thread.sleep(5000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

}
