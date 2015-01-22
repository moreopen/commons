package com.moreopen.commons.redis.sharding;

public class SequenceJedisShardInfo {
	private String host;
	private int startPort;
	private int endPort;

	public SequenceJedisShardInfo(String host, String startEndPort) {
		this.host = host;
		String[] startEnd = startEndPort.split("-");
		if (startEnd.length == 2) {
			startPort = Integer.parseInt(startEnd[0]);
			endPort = Integer.parseInt(startEnd[1]);
		}
	}

	public SequenceJedisShardInfo(String host, int startPort, int endPort) {
		this.host = host;
		this.startPort = startPort;
		this.endPort = endPort;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getStartPort() {
		return startPort;
	}

	public void setStartPort(int startPort) {
		this.startPort = startPort;
	}

	public int getEndPort() {
		return endPort;
	}

	public void setEndPort(int endPort) {
		this.endPort = endPort;
	}

}
