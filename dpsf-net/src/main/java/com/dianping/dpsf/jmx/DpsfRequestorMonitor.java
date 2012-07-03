/**
 * Project: ${dpsf-net.aid}
 * 
 * File Created at 2011-8-22
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
package com.dianping.dpsf.jmx;

import java.lang.Thread.State;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.control.PigeonConfig;
import com.dianping.dpsf.net.channel.Client;
import com.dianping.dpsf.net.channel.config.HostInfo;
import com.dianping.dpsf.net.channel.netty.NettyClientManager;
import com.dianping.dpsf.stat.RpcStatsPool;
import com.dianping.dpsf.stat.RpcStatsPool.ServiceBarrel;
import com.dianping.hawk.jmx.support.MBeanMeta;

/**
 * Monitor dpsf execution information as requestor
 * @author danson.liu
 *
 */
public class DpsfRequestorMonitor {
	
	private static final Logger logger = DPSFLog.getLogger();
	private static DpsfRequestorMonitor instance = new DpsfRequestorMonitor();
	
	private NettyClientManager clientManager;

	@MBeanMeta(ignore = true)
	public static DpsfRequestorMonitor getInstance() {
		return instance;
	}
	
	private DpsfRequestorMonitor() {
	}
	
	@MBeanMeta(order = 0)
	public String getHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return "unknown";
		}
	}
	
	@MBeanMeta(order = 1)
	public Collection<String> getConnectedServers() {
		Collection<String> connectedServers = new HashSet<String>();
		Map<String, Client> requestorStubs = getAllRequestStub();
		for (Entry<String, Client> stub : requestorStubs.entrySet()) {
			if (!getDisconnectedRequestStub().containsKey(stub.getKey())) {
				connectedServers.add(stub.getKey());
			}
		}
		return connectedServers;
	}
	
	@MBeanMeta(order = 2)
	public Collection<String> getDisconnectedServers() {
		Collection<String> disconnectedServers = new ArrayList<String>();
		for (Entry<String, Client> stub : getDisconnectedRequestStub().entrySet()) {
			disconnectedServers.add(stub.getKey());
		}
		return disconnectedServers;
	}
	
	public ServiceInvokeStatsResult getServiceStats(int skip, int size) {
		try {
			ServiceInvokeStatsResult statsResult = new ServiceInvokeStatsResult();
			Map<String, ServiceInvokeStats> serviceStats = new HashMap<String, ServiceInvokeStats>();
			Map<String, Set<HostInfo>> serviceHostInfos = clientManager.getServiceHostInfos();
			Map<String, Set<String>> service2Connects = new TreeMap<String, Set<String>>();
			Map<String, Set<String>> service2FullNames = new HashMap<String, Set<String>>();
			for (String serviceFullName : serviceHostInfos.keySet()) {
				String serviceName = getServiceName(serviceFullName);
				if (serviceName != null) {
					Set<String> connects = service2Connects.get(serviceName);
					if (connects == null) {
						connects = new TreeSet<String>();
						service2Connects.put(serviceName, connects);
					}
					Set<HostInfo> hostInfos = serviceHostInfos.get(serviceFullName);
					Iterator<HostInfo> iter = hostInfos.iterator();
					while (iter.hasNext()) {
						connects.add(iter.next().getConnect());
					}
					if (!service2FullNames.containsKey(serviceName)) {
						service2FullNames.put(serviceName, new HashSet<String>());
					}
					service2FullNames.get(serviceName).add(serviceFullName);
				}
			}
			int index = 0;
			for (Entry<String, Set<String>> entry : service2Connects.entrySet()) {
				if (index >= skip && index < skip + size) {
					String service = entry.getKey();
					serviceStats.put(service, new ServiceInvokeStats(index));
					for (String connect : entry.getValue()) {
						ServiceInvokeStats serviceInvokeStats = serviceStats.get(service);
						ServiceInvokeStat stat = new ServiceInvokeStat(connect);
						if (RpcStatsPool.hasServerBarrel(connect)) {
							ServiceBarrel serverBarrel = RpcStatsPool.getServerBarrel(connect);
							/*
							 * 目前console中weight只设定到connect上, 但pigeon支持可设定到service+connect上, 监控UI上也只显示service分类名,
							 * 不区分全名, 所以这里也仅拿其中任一service+connect组合的weight显示
							 */
							stat.setCapacity(new BigDecimal(serverBarrel.getCapacity()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
							stat.setRequestSend(serverBarrel.getTotalRequest().longValue());
							stat.setOnewayRequestSend(serverBarrel.getOnewayRequest().longValue());
							stat.setRequestSendLastSec(serverBarrel.getLastSecondRequest());
						}
						stat.setWeight(getServiceWeight(service2FullNames, service, connect));
						stat.setConnected(!clientManager.getReconnectTask().getClosedClients().containsKey(connect));
						Map<String, Client> allClients = clientManager.getClientCache().getAllClients();
						if (allClients.containsKey(connect)) {
							stat.setActive(allClients.get(connect).isActive());
						}
						serviceInvokeStats.addServiceStat(stat);
					}
				}
				index++;
			}
			statsResult.setStats(serviceStats);
			statsResult.setTotal(service2Connects.size());
			return statsResult;
		} catch (RuntimeException e) {
			logger.error("Get servicestats failed.", e);
			throw e;
		}
	}
	
	private int getServiceWeight(Map<String, Set<String>> service2FullNames, String serviceName, String connect) {
		Set<String> serviceFullNames = service2FullNames.get(serviceName);
		if (serviceFullNames != null) {
			for (String serviceFullName : serviceFullNames) {
				Integer weight = clientManager.getRouterManager().getWeight(serviceFullName, connect);
				if (weight != null) {
					return weight;
				}
			}
		}
		return 1;
	}
	
	/**
	 * 
	 * @param serviceFullName  形如http://service.dianping.com/cacheService/cacheConfigService_1.0.0
	 * @return 形如http://service.dianping.com/cacheService
	 */
	private String getServiceName(String serviceFullName) {
		String serviceSubName = StringUtils.substringBetween(serviceFullName, PigeonConfig.getServiceNameSpace(), "/");
		if (serviceSubName != null) {
			return PigeonConfig.getServiceNameSpace() + serviceSubName;
		}
		return null;
	}

	@MBeanMeta(order = 3)
	public int getThreadPoolSize() {
		return getThreadPool().getPoolSize();
	}
	
	@MBeanMeta(order = 4)
	public int getWaitTaskInQueue() {
		return getThreadPool().getQueue().size();
	}
	
	@MBeanMeta(order = 5)
	public int getActiveThreadCount() {
		return getThreadPool().getActiveCount();
	}

	@MBeanMeta(order = 6)
	public int getBlockedThreadCount() {
		return getThreadCount(State.BLOCKED);
	}
	
	@MBeanMeta(order = 7)
	public int getNewThreadCount() {
		return getThreadCount(State.NEW);
	}
	
	@MBeanMeta(order = 8)
	public int getRunnableThreadCount() {
		return getThreadCount(State.RUNNABLE);
	}
	
	@MBeanMeta(order = 9)
	public int getTerminatedThreadCount() {
		return getThreadCount(State.TERMINATED);
	}

	@MBeanMeta(order = 10)
	public int getTimedWaitingThreadCount() {
		return getThreadCount(State.TIMED_WAITING);
	}
	
	@MBeanMeta(order = 11)
	public int getWaitingThreadCount() {
		return getThreadCount(State.WAITING);
	}
	
	private int getThreadCount(State state) {
		ThreadGroup threadGroup = clientManager.getClientResponseThreadPool().getFactory().getGroup();
		Thread[] threads = new Thread[threadGroup.activeCount()];
		threadGroup.enumerate(threads, false);
		int threadCount = 0;
		for(Thread t : threads) {
			if (state == t.getState()) {
				threadCount++;
			}
		}
		return threadCount;
	}
	
	private ThreadPoolExecutor getThreadPool() {
		return clientManager.getClientResponseThreadPool().getExecutor();
	}
	
	private Map<String, Client> getAllRequestStub() {
		return clientManager.getClientCache().getAllClients();
	}
	
	private Map<String, Client> getDisconnectedRequestStub() {
		return clientManager.getReconnectTask().getClosedClients();
	}

	@MBeanMeta(ignore = true)
	public void setClientManager(NettyClientManager clientManager) {
		this.clientManager = clientManager;
	}
	
}
