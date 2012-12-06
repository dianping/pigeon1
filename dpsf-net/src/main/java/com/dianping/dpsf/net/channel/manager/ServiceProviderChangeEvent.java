/**
 * Project: dpsf-net
 * 
 * File Created at 2011-8-15
 * $Id$
 * 
 * Copyright 2011 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dianping.dpsf.net.channel.manager;

/**
 * 动态服务信息
 * 
 * @author marsqing
 * 
 */
public class ServiceProviderChangeEvent {

	private String serviceName;
	private String host;
	private int port;
	private int weight;
	private String connect;

	public String getServiceName() {
		return serviceName;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public int getWeight() {
		return weight;
	}

	public String getConnect() {
		return connect;
	}

	public ServiceProviderChangeEvent(String serviceName, String host, int port, int weight) {
		this.serviceName = serviceName;
		this.host = host;
		this.port = port;
		this.weight = weight;
		this.connect = host + ":" + port;
	}

}
