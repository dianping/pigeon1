/**
 * Project: ${dpsf-net.aid}
 * 
 * File Created at 2012-3-17
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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.control.PigeonConfig;
import com.dianping.dpsf.exception.DPSFException;
import com.dianping.dpsf.net.channel.cluster.LoadBalance;
import com.dianping.dpsf.net.channel.cluster.loadbalance.LoadAutoawareLoadBalance;
import com.dianping.dpsf.net.channel.cluster.loadbalance.RandomLoadBalance;

/**
 * LoadBalance[注册|获取]管理器
 * @author danson.liu
 *
 */
public class LoadBalanceManager {

	private static Logger logger = DPSFLog.getLogger();
	
	private  static Map<String, LoadBalance> loadBalanceMap = new HashMap<String, LoadBalance>();
	
	public static Map<String, LoadBalance> builtInBalances = new HashMap<String, LoadBalance>();
	private static LoadBalance defaultLoadBalance = RandomLoadBalance.instance;

	private static volatile int errorLogSeed = 0;
	
	public static LoadBalance getLoadBalance(String serviceName, String group, int callType) {
		if (callType == Constants.CALLTYPE_NOREPLY) {
			return RandomLoadBalance.instance;
		}
		String serviceId = serviceName + ":" + group;
		LoadBalance loadBalance = loadBalanceMap.get(serviceId);
		if (loadBalance != null) {
			return loadBalance;
		}
		if (PigeonConfig.getLoadBalanceSetByJmx() != null) {
			LoadBalance balanceFromJmx = builtInBalances.get(PigeonConfig.getLoadBalanceSetByJmx());
			if (balanceFromJmx != null) {
				return balanceFromJmx;
			}
		}
		String balanceConfigFromLion = PigeonConfig.getLoadBalanceFromLion();
		if (balanceConfigFromLion != null) {
			LoadBalance balanceFromLion = builtInBalances.get(balanceConfigFromLion);
			if (balanceFromLion != null) {
				return balanceFromLion;
			} else {
				logError("Loadbalance[" + balanceConfigFromLion + "] set in lion is invalid, only support " + builtInBalances.keySet() + ".", null);
			}
		}
		return defaultLoadBalance;
	}
	
	@SuppressWarnings("unchecked")
	public static void register(String serviceName, String group, Object loadBalance) {
		String serviceId = serviceName + ":" + group;
		if (loadBalanceMap.containsKey(serviceId)) {
			logger.warn("Duplicate loadbalance already registered with service[" + serviceId + "], replace it.");
		}
		LoadBalance loadBlanceObj = null;
		if (loadBalance instanceof LoadBalance) {
			loadBlanceObj = (LoadBalance) loadBalance;
		} else {
			if (loadBalance instanceof String) {
				if (!builtInBalances.containsKey(loadBalance)) {
					throw new DPSFException("Loadbalance[" + loadBalance + "] registered by service[" + serviceId + "] is not supported.");
				}
				loadBlanceObj = builtInBalances.get(loadBalance);
			} else if (loadBalance instanceof Class) {
				Class<? extends LoadBalance> loadBalanceClass = (Class<? extends LoadBalance>) loadBalance;
				try {
					loadBlanceObj = loadBalanceClass.newInstance();
				} catch (Exception e) {
					throw new DPSFException("Register loadbalance[service=" + serviceId + ", class=" + loadBalance + "] failed.", e);
				}
			}
		}
		if (loadBlanceObj != null) {
			loadBalanceMap.put(serviceId, loadBlanceObj);
		}
	}
	
	private static void logError(String message, Throwable t) {
		if (errorLogSeed++ % 1000 == 0) {
			if (t != null) {
				logger.warn(message, t);
			} else {
				logger.warn(message);
			}
			errorLogSeed = 0;
		}
	}
	
	static {
		builtInBalances.put(RandomLoadBalance.NAME, RandomLoadBalance.instance);
		builtInBalances.put(LoadAutoawareLoadBalance.NAME, LoadAutoawareLoadBalance.instance);
	}
	
}
