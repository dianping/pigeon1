/**
 * Project: ${dpsf-net.aid}
 * 
 * File Created at 2012-3-24
 * $Id$
 * 
 * Copyright 2010 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dianping.dpsf.net.channel.config;

/**
 * Host information refactor from NettyClientManager inner static class
 * 
 * @author danson.liu
 * 
 */
public class HostInfo {
	private String connect;
	private String host;
	private int port;
	private int weight;

	public HostInfo(String host, int port, int weight) {
		this.host = host;
		this.port = port;
		this.connect = host + ":" + port;
		this.weight = weight;
	}

	public HostInfo(String connect, int weight) {
		int colonIdx = connect.indexOf(":");
		this.connect = connect;
		this.host = connect.substring(0, colonIdx);
		this.port = Integer.parseInt(connect.substring(colonIdx + 1));
		this.weight = weight;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof HostInfo) {
			HostInfo hp = (HostInfo) obj;
			return this.host.equals(hp.host) && this.port == hp.port;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return host.hashCode() + port;
	}

	@Override
	public String toString() {
		return "HostInfo [host=" + host + ", port=" + port + ", weight="
				+ weight + "]";
	}

	public String getConnect() {
		return connect;
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

}
