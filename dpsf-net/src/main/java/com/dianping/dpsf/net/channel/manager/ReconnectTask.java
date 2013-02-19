/**
 * Project: ${dpsf-net.aid}
 * 
 * File Created at 2012-3-22
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
package com.dianping.dpsf.net.channel.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;

import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.control.PigeonConfig;
import com.dianping.dpsf.exception.NetException;
import com.dianping.dpsf.net.channel.Client;
import com.dianping.dpsf.net.channel.config.ClusterConfigure;
import com.dianping.dpsf.net.channel.config.ClusterListener;
import com.dianping.dpsf.net.channel.config.Configure;
import com.dianping.dpsf.net.channel.config.ConnectMetaData;
import com.dianping.dpsf.net.channel.netty.client.NettyClient;

/**
 * Reconnect disconnected clients
 * @author danson.liu
 *
 */
public class ReconnectTask implements Runnable, ClusterListener {
	
	private static Logger logger = DPSFLog.getLogger();

	private final ClientManager clientManager;
	
	private ConcurrentMap<String,Client> closedClients = new ConcurrentHashMap<String,Client>();

    private final Configure clusterConfigure;

	public ReconnectTask(ClientManager clientManager, Configure clusterConfigure) {
		this.clientManager = clientManager;
        this.clusterConfigure = clusterConfigure;
	}

	@Override
	public void run() {
		long sleepTime = PigeonConfig.getReconnectInterval();
		while (!Thread.currentThread().isInterrupted()) {
			try {
				Thread.sleep(sleepTime);
				long now = System.currentTimeMillis();
				//连接已经断开的Clients
				for(String connect : this.closedClients.keySet()) {
					Client client = this.closedClients.get(connect);
					if(!client.isConnected()) {
						try {
							client.connect();
						} catch (NetException e) {
							logger.error("Connect server[" + client.getAddress() + "] failed, detail[" + e.getMessage() + "].");
						}
					}
					if(client.isConnected()) {
						//加回去时active设置为true
					    clusterConfigure.addConnect(connect,client);
						this.closedClients.remove(connect);
					}
				}
				sleepTime = PigeonConfig.getReconnectInterval() - (System.currentTimeMillis() - now);
			} catch(Exception e) {
				logger.error("Do reconnect task failed, detail[" + e.getMessage() + "].");
			} finally {
				if(sleepTime < 1000) {
					sleepTime = 1000;
				}
			}
		}
	}

	@Override
	public void addConnect(ConnectMetaData cmd) {
	}

	@Override
	public void addConnect(ConnectMetaData cmd, Client client) {
	}

	@Override
	public void removeConnect(ConnectMetaData cmd) {
		Client client = new NettyClient(cmd.getHost(),cmd.getPort(),this.clientManager);
		this.closedClients.putIfAbsent(cmd.getConnect(), client);
	}

	@Override
	public void doNotUse(String serviceName, String host, int port) {
	}

	public Map<String, Client> getClosedClients() {
		return closedClients;
	}

}
