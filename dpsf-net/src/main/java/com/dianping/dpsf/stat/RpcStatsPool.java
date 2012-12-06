/**
 * Project: ${dpsf-net.aid}
 * 
 * File Created at 2012-3-20
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
package com.dianping.dpsf.stat;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.control.PigeonConfig;

/**
 * Remote procedure call statistics pool
 * @TODO 暂不使用CentralStatService类, 埋的点不太符合这边的要求, 重构时考虑整合方案
 * @author danson.liu
 *
 */
public class RpcStatsPool {
	
	private static Logger logger = DPSFLog.getLogger();
	
	private static ConcurrentMap<String, ServiceBarrel> 	serverStatBarrels 	= new ConcurrentHashMap<String, ServiceBarrel>();
	
	static {
		new ServiceBarrelExpiredRequestChecker().start();
	}
	
	public static void flowIn(DPSFRequest request, String toServer) {
		if (checkRequestNeedStat(request)) {
			ServiceBarrel barrel = getServerBarrel(toServer);
			if (barrel != null) {
				barrel.flowIn(request);
			} else {
				logger.error("Got a null barrel with server[" + toServer + "] in flowIn operation.");
			}
		}
	}

	public static void flowOut(DPSFRequest request, String fromServer) {
		if (checkRequestNeedStat(request)) {
			ServiceBarrel barrel = getServerBarrel(fromServer);
			if (barrel != null) {
				barrel.flowOut(request);
			} else {
				logger.error("Got a null barrel with server[" + fromServer + "] in flowOut operation.");
			}
		}
	}

	public static float getCapacity(String server) {
		ServiceBarrel barrel = serverStatBarrels.get(server);
		return barrel != null ? barrel.getCapacity() : 0f;
	}
	
	private static boolean checkRequestNeedStat(DPSFRequest request) {
		return request != null && request.getMessageType() == Constants.MESSAGE_TYPE_SERVICE;
	}

	public static ServiceBarrel getServerBarrel(String server) {
		ServiceBarrel barrel = serverStatBarrels.get(server);
		if (barrel == null) {
			ServiceBarrel newBarrel = new ServiceBarrel(server);
			barrel = serverStatBarrels.putIfAbsent(server, newBarrel);
			if (barrel == null) {
				barrel = newBarrel;
			}
		}
		return barrel;
	}
	
	public static boolean hasServerBarrel(String server) {
		return serverStatBarrels.containsKey(server);
	}
	
	@SuppressWarnings("serial")
	public static class ServiceBarrel implements Serializable {
		private String address;
		private volatile float capacity = 0f;
		private Set<Long> requestSeqs = Collections.newSetFromMap(new ConcurrentHashMap<Long, Boolean>());
		//桶中某些容量因某些意外因素导致没有释放, 可以用这个进行Check
		private Map<Long, Object[]> requestSeqDetails = new ConcurrentHashMap<Long, Object[]>();
		
		private AtomicLong totalRequest = new AtomicLong();		//total request send
		private AtomicLong onewayRequest = new AtomicLong();	//total oneway request send
		
		private Map<Integer, AtomicInteger> totalRequestInSecond = new HashMap<Integer, AtomicInteger>();
		
		private Lock capacityLock = new ReentrantLock();
		
		public ServiceBarrel(String address) {
			this.address = address;
			preFillData();	//为了更优地计算每秒请求数, 使用预填数据代替同步数据结构
		}

		public void flowIn(DPSFRequest request) {
			Calendar now = Calendar.getInstance();
			totalRequest.incrementAndGet();
			if (request.getCallType() == Constants.CALLTYPE_NOREPLY) {
				onewayRequest.incrementAndGet();
			}
			incrementTotalRequestInSecond(now.get(Calendar.SECOND));
			if (request.getCallType() == Constants.CALLTYPE_REPLY) {
				Float flow = (Float) request.getAttachment(Constants.REQ_ATTACH_FLOW);
				if (flow != null) {
					refreshCapacity(flow);
				}
				this.requestSeqs.add(request.getSequence());
				this.requestSeqDetails.put(request.getSequence(), new Object[] {now.getTimeInMillis(), request.getTimeout(), flow});
			}
		}

		public void flowOut(DPSFRequest request) {
			if (request.getCallType() == Constants.CALLTYPE_REPLY) {
				flowOut(request.getSequence(), (Float) request.getAttachment(Constants.REQ_ATTACH_FLOW));
			}
		}
		
		public void flowOut(long requestSeq, Float flow) {
			if (requestSeqs.remove(requestSeq) && flow != null) {
				refreshCapacity(-1 * flow);
			}
			requestSeqDetails.remove(requestSeq);
		}
		
		public int getLastSecondRequest() {
			int lastSecond = Calendar.getInstance().get(Calendar.SECOND) - 1;
			lastSecond = lastSecond >= 0 ? lastSecond : lastSecond + 60;
			AtomicInteger counter = totalRequestInSecond.get(lastSecond);
			return counter != null ? counter.intValue() : 0;
		}
		
		private void incrementTotalRequestInSecond(int second) {
			if (PigeonConfig.isSpeedStatEnabled()) {
				AtomicInteger counter = totalRequestInSecond.get(second);
				if (counter != null) {
					counter.incrementAndGet();
				} else {
					logger.error("Impossible case happended, second[" + second + "]'s request counter is null.");
				}
			}
		}

		private void refreshCapacity(float addition) {
			capacityLock.lock();
			try {
				this.capacity += addition;
			} finally {
				capacityLock.unlock();
			}
		}
		
		/**
		 * 重置过期的每秒请求数计数器
		 */
		public void resetRequestInSecondCounter() {
			int second = Calendar.getInstance().get(Calendar.SECOND);
			int prev3Sec = second - 10;
			for (int i = 1; i <= 30; i++) {
				int prevSec = prev3Sec - i;
				prevSec = prevSec >= 0 ? prevSec : prevSec + 60;
				AtomicInteger counter = totalRequestInSecond.get(prevSec);
				if (counter != null) {
					counter.set(0);
				}
			}
		}

		private void preFillData() {
			for (int sec = 0; sec < 60; sec++) {
				totalRequestInSecond.put(sec, new AtomicInteger());
			}
		}

		public String getAddress() {
			return address;
		}

		public float getCapacity() {
			return capacity;
		}

		public AtomicLong getTotalRequest() {
			return totalRequest;
		}

		public AtomicLong getOnewayRequest() {
			return onewayRequest;
		}
	}
	
	static class ServiceBarrelExpiredRequestChecker extends Thread {
		private static int nextThreadNumber = 0;
		public ServiceBarrelExpiredRequestChecker() {
			setDaemon(true);
			setName("PigeonThread-" + getClass().getSimpleName() + "-" + nextThreadNumber++);
		}
		
		@Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				try {Thread.sleep(5000);} catch (InterruptedException e) {}
				if (serverStatBarrels != null) {
					try {
						long currentTimeMillis = System.currentTimeMillis();
						for (ServiceBarrel barrel : serverStatBarrels.values()) {
							barrel.resetRequestInSecondCounter();
							try {
								Map<Long, Float> expiredRequests = new HashMap<Long, Float>();
								for (Iterator<Entry<Long, Object[]>> iter = barrel.requestSeqDetails.entrySet().iterator(); iter.hasNext();) {
									Entry<Long, Object[]> detailEntry = iter.next();
									Object[] details = detailEntry.getValue();
									long requestFlowInTime = (Long) details[0];
									int requestTimeout = (Integer) details[1];
									Float requestFlow = (Float) details[2];
									if (currentTimeMillis - requestFlowInTime >= 2 * requestTimeout) {
										expiredRequests.put(detailEntry.getKey(), requestFlow);
									}
								}
								for (Entry<Long, Float> expiredEntry : expiredRequests.entrySet()) {
									barrel.flowOut(expiredEntry.getKey(), expiredEntry.getValue());
								}
							} catch (Exception e) {
								logger.error("Check expired request in service barrel failed, detail[" + e.getMessage() + "].");
							}
						}
					} catch (Exception e) {
						logger.error("Check expired request in service barrel failed, detail[" + e.getMessage() + "].");
					}
				}
			}
		}
	}
	
}
