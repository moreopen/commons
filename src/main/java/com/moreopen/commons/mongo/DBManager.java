package com.moreopen.commons.mongo;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;

public class DBManager implements InitializingBean, DisposableBean {
	
	private Mongo mongo;
	
	private String serverAddresses;
	
	private int connectionsPerHost = 20;
	
	/**
	 *  private static final ReadPreference _PRIMARY;  0
     *	private static final ReadPreference _SECONDARY; 1
     *	private static final ReadPreference _SECONDARY_PREFERRED; 2
     *	private static final ReadPreference _PRIMARY_PREFERRED; 3
     *	private static final ReadPreference _NEAREST; 4
	 */
	private int readPreferenceType = 0;
	
	/**
	 * please {@see MongoOptions}
	 */
	private int connectTimeout = 5000;
	
	private int socketTimeout = 5000;
	
	private int maxWaitTime = 30000;

	@Override
	public void afterPropertiesSet() throws Exception {
		List<ServerAddress> list = parseServerAddresses(serverAddresses);
		if (list.size() == 1) {
			mongo = new Mongo(list.get(0));
		} else {
			mongo = new Mongo(list);
		}
		MongoOptions mongoOptions = mongo.getMongoOptions();
		mongoOptions.setAutoConnectRetry(true);
		mongoOptions.setConnectTimeout(connectTimeout);
		mongoOptions.setConnectionsPerHost(connectionsPerHost);
		mongoOptions.setSocketTimeout(socketTimeout);
		mongoOptions.setMaxWaitTime(maxWaitTime);

		if (readPreferenceType == 0) {
			mongo.setReadPreference(ReadPreference.primary());
		} else if (readPreferenceType == 1) {
			mongo.setReadPreference(ReadPreference.secondary());
		} else if (readPreferenceType == 2) {
			mongo.setReadPreference(ReadPreference.secondaryPreferred());
		} else if (readPreferenceType == 3) {
			mongo.setReadPreference(ReadPreference.primaryPreferred());
		} else if (readPreferenceType == 4) {
			mongo.setReadPreference(ReadPreference.nearest());
		} else {
			//use default
			mongo.setReadPreference(ReadPreference.primary());
		}
		System.out.println(String.format("Mongo ReadPreference config, type [%s], ReadPreference [%s]", readPreferenceType, mongo.getReadPreference()));
//		mongo.setReadPreference(ReadPreference.secondaryPreferred());
	}
	
	@Override
	public void destroy() throws Exception {
		if (mongo != null) {
			mongo.close();
		}
	}
	
	public DB getDB(String dbname) {
		return mongo.getDB(dbname);
	}

	public void setConnectionsPerHost(int connectionsPerHost) {
		this.connectionsPerHost = connectionsPerHost;
	}
	
	public void setServerAddresses(String serverAddresses) {
		this.serverAddresses = serverAddresses;
	}

	/**
	 * Split a string in the form of
	 * "host1:port1,host2:port2" into a list of InetSocketAddress instances
	 *
	 * @return List<ServerAddress>
	 * @throws UnknownHostException 
	 * @throws NumberFormatException 
	 */
	protected List<ServerAddress> parseServerAddresses(String servers) throws NumberFormatException, UnknownHostException {
		List<ServerAddress> list = new ArrayList<ServerAddress>();
		StringTokenizer tokenizer = new StringTokenizer(servers, ",");
		while (tokenizer.hasMoreTokens()) {
			String each = tokenizer.nextToken();
			String[] splitted = each.split(":");
			if (splitted.length == 2) {
				list.add(new ServerAddress(splitted[0], Integer.parseInt(splitted[1])));
			}
		}
		Assert.isTrue(!list.isEmpty());
		return list;
	}

	public void setReadPreferenceType(int readPreferenceType) {
		this.readPreferenceType = readPreferenceType;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public void setMaxWaitTime(int maxWaitTime) {
		this.maxWaitTime = maxWaitTime;
	}

}
