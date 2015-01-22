package com.moreopen.commons.redis.sharding;

import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.JedisShardInfo;

public class SequenceJedisShardInfoList {

	private List<SequenceJedisShardInfo> sequenceShards;

	public SequenceJedisShardInfoList(
			List<SequenceJedisShardInfo> sequenceShards) {
		this.sequenceShards = sequenceShards;
	}

	public List<JedisShardInfo> getJedisShardInfos() {
		List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
		for (SequenceJedisShardInfo sequenceJedisShardInfo : sequenceShards) {
			if (sequenceJedisShardInfo.getStartPort() > sequenceJedisShardInfo
					.getEndPort()) {
				throw new RuntimeException();
			}
			for (int i = sequenceJedisShardInfo.getStartPort(); i <= sequenceJedisShardInfo
					.getEndPort(); ++i) {
				JedisShardInfo jedisShardInfo = new JedisShardInfo(
						sequenceJedisShardInfo.getHost(), i);
				shards.add(jedisShardInfo);
			}
		}
		return shards;
	}

}
