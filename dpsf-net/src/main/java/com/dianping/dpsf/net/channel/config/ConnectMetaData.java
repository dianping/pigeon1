/**
 * 
 */
package com.dianping.dpsf.net.channel.config;

import java.util.HashMap;
import java.util.Map;

/**    
 * <p>    
 * Title: ConnnectConfig.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-9-27 下午04:23:41   
 */
public class ConnectMetaData {
	
	public static final String PLACEHOLDER = ":";
	
	private String connect;
	
	private String host;
	
	private int port;
	
	private Map<String,Integer> serviceNames;
	
	public ConnectMetaData(String serviceName,String connect,int weight){
		
		this(new HashMap<String,Integer>(),connect);
		this.serviceNames.put(serviceName,weight);
	}
	
	public ConnectMetaData(Map<String,Integer> serviceNames,String connect){
		this.serviceNames = serviceNames;
		this.connect = connect;
		String[] connectMetaData = connect.split(PLACEHOLDER);
		this.host = connectMetaData[0];
		this.port = Integer.parseInt(connectMetaData[1]);
	}
	
	public void addServiceNames(Map<String,Integer> serviceNames){
		this.serviceNames.putAll(serviceNames);
	}

	/**
	 * @return the connect
	 */
	public String getConnect() {
		return connect;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return the serviceNames
	 */
	public Map<String,Integer> getServiceNames() {
		return serviceNames;
	}

}
