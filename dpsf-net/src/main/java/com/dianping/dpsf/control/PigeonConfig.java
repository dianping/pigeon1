/**
 * Project: ${dpsf-net.aid}
 * 
 * File Created at 2012-3-21
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
package com.dianping.dpsf.control;

import org.apache.log4j.Logger;

import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.net.channel.manager.LoadBalanceManager;
import com.dianping.hawk.jmx.HawkJMXUtil;
import com.dianping.lion.client.ConfigCache;

/**
 * Pigeon关于JMX和Lion的配置中心
 * 
 * @author danson.liu
 * 
 */
public class PigeonConfig {

	private static Logger logger = DPSFLog.getLogger();

	private static volatile PigeonConfig INSTANCE;
	private static volatile int errorLogSeed = 0;

	private boolean isHawkApiValid;
	private boolean isLionApiValid;

	private String loadBalanceSetByJmx;
	private Boolean pigeonSpeedEnabledSetByJmx;
	private boolean isHeartBeatResponseSetByJmx = true;

	private static final String LION_LOADBALANCE = "pigeon.loadbalance";
	private static final String LION_RECONNECT_INTERVAL = "pigeon.reconnect.interval";
	private static final String LION_HEARTBEAT_INTERVAL = "pigeon.heartbeat.interval";
	private static final String LION_HEARTBEAT_TIMEOUT = "pigeon.heartbeat.timeout";
	private static final String LION_HEARTBEAT_DEADTHRESHOLD = "pigeon.heartbeat.dead.threshold";
	private static final String LION_HEARTBEAT_HEALTHTHRESHOLD = "pigeon.heartbeat.health.threshold";
	private static final String LION_HEARTBEAT_AUTOPICKOFF = "pigeon.heartbeat.autopickoff";
	private static final String LION_SERVICE_NAMESPACE = "pigeon.service.namespace";
	private static final String LION_PIGEON_SPEED_ENABLED = "pigeon.speed.enabled";
	private static final String LION_WRITE_BUFFER_HIGH_WATER = "pigeon.channel.writebuff.high";
	private static final String LION_WRITE_BUFFER_LOW_WATER = "pigeon.channel.writebuff.low";
	private static final String LION_DEFAULT_WRITE_BUFF_LIMIT = "pigeon.channel.writebuff.defaultlimit";

	private static final long DEFAULT_RECONNECT_INTERVAL = 3000;
	private static final long DEFAULT_HEARTBEAT_INTERVAL = 3000;
	private static final long DEFAULT_HEARTBEAT_TIMEOUT = 5000;
	private static final long DEFAULT_HEARTBEAT_DEADCOUNT = 5;
	private static final long DEFAULT_HEARTBEAT_HEALTHCOUNT = 5;
	private static final boolean DEFAULT_HEARTBEAT_AUTOPICKOFF = true;
	private static final String DEFAULT_SERVICE_NAMESPACE = "http://service.dianping.com/";
	private static final Boolean DEFAULT_PIGEON_SPEED_ENABLED = false;
	private static final int DEFAULT_WRITE_BUFFER_HIGH_WATER = 35 * 1024 * 1024;
	private static final int DEFAULT_WRITE_BUFFER_LOW_WATER = 25 * 1024 * 1024;
	private static final boolean DEFAULT_WRITE_BUFF_LIMIT = false;

	private PigeonConfig() {
		checkHawkAndLionStatus();
		if (isHawkApiValid) {
			HawkJMXUtil.registerMBean("PigeonConfiguration", this);
		}
	}

	public static PigeonConfig getInstance() {
		if (INSTANCE == null) {
			synchronized (PigeonConfig.class) {
				if (INSTANCE == null) {
					INSTANCE = new PigeonConfig();
				}
			}
		}
		return INSTANCE;
	}

	public static boolean isLionApiValid() {
		return getInstance().isLionApiValid;
	}

	public static boolean isHawkApiValid() {
		return getInstance().isHawkApiValid;
	}

	public static String getLoadBalanceSetByJmx() {
		return getInstance().loadBalanceSetByJmx;
	}

	public static String getLoadBalanceFromLion() {
		return getStringValueFromLion(LION_LOADBALANCE, null);
	}

	public static boolean isHeartBeatAutoPickOff() {
		return getBooleanValueFromLion(LION_HEARTBEAT_AUTOPICKOFF, DEFAULT_HEARTBEAT_AUTOPICKOFF);
	}

	public static boolean isHeartBeatResponse() {
		return getInstance().isHeartBeatResponseSetByJmx;
	}

	public static long getReconnectInterval() {
		return getLongValueFromLion(LION_RECONNECT_INTERVAL, DEFAULT_RECONNECT_INTERVAL);
	}

	public static long getHeartBeatInterval() {
		return getLongValueFromLion(LION_HEARTBEAT_INTERVAL, DEFAULT_HEARTBEAT_INTERVAL);
	}

	/**
	 * 心跳是否超时的判断阀值
	 * 
	 * @return
	 */
	public static long getHeartBeatTimeout() {
		return getLongValueFromLion(LION_HEARTBEAT_TIMEOUT, DEFAULT_HEARTBEAT_TIMEOUT);
	}

	/**
	 * 获取心跳检测服务端假死的依据, 心跳连续超时的次数值
	 * 
	 * @return
	 */
	public static long getHeartBeatDeadCount() {
		return getLongValueFromLion(LION_HEARTBEAT_DEADTHRESHOLD, DEFAULT_HEARTBEAT_DEADCOUNT);
	}

	/**
	 * 获取心跳检测服务端正常的依据, 心跳连续正常返回的次数值
	 * 
	 * @return
	 */
	public static long getHeartBeatHealthCount() {
		return getLongValueFromLion(LION_HEARTBEAT_HEALTHTHRESHOLD, DEFAULT_HEARTBEAT_HEALTHCOUNT);
	}

	/**
	 * 返回service命名的前缀空间, 如http://service.dianping.com/cacheService/cacheConfigService_1.0.0中的http://service.dianping.com/
	 * 
	 * @return
	 */
	public static String getServiceNameSpace() {
		return getStringValueFromLion(LION_SERVICE_NAMESPACE, DEFAULT_SERVICE_NAMESPACE);
	}

	public static boolean isSpeedStatEnabled() {
		Boolean isPigeonSpeedEnabled = getInstance().pigeonSpeedEnabledSetByJmx;
		if (isPigeonSpeedEnabled == null && isLionApiValid()) {
			try {
				isPigeonSpeedEnabled = ConfigCache.getInstance().getBooleanProperty(LION_PIGEON_SPEED_ENABLED);
			} catch (Exception e) {
				logLionError(LION_PIGEON_SPEED_ENABLED);
			}
		}
		return isPigeonSpeedEnabled != null ? isPigeonSpeedEnabled : DEFAULT_PIGEON_SPEED_ENABLED;
	}

	public static boolean getDefaultWriteBufferLimit() {
		return getBooleanValueFromLion(LION_DEFAULT_WRITE_BUFF_LIMIT, DEFAULT_WRITE_BUFF_LIMIT);
	}

	public static int getWriteBufferHighWater() {
		return getIntValueFromLion(LION_WRITE_BUFFER_HIGH_WATER, DEFAULT_WRITE_BUFFER_HIGH_WATER);
	}

	public static int getWriteBufferLowWater() {
		return getIntValueFromLion(LION_WRITE_BUFFER_LOW_WATER, DEFAULT_WRITE_BUFFER_LOW_WATER);
	}

	public String setLoadBalanceByJmx(String loadBalance) {
		if (LoadBalanceManager.builtInBalances.containsKey(loadBalance)) {
			this.loadBalanceSetByJmx = loadBalance;
			return "Succeed";
		} else {
			return "Failed: invalid loadbalance, only support " + LoadBalanceManager.builtInBalances.keySet() + ".";
		}
	}

	public String getBuiltInLoadBalances() {
		return LoadBalanceManager.builtInBalances.keySet().toString();
	}

	public String clearLoadBalanceSetByJmx() {
		this.loadBalanceSetByJmx = null;
		return "Succeed";
	}

	public void setPigeonSpeedEnabledByJmx() {
		this.pigeonSpeedEnabledSetByJmx = true;
	}

	public void setPigeonSpeedDisabledByJmx() {
		this.pigeonSpeedEnabledSetByJmx = false;
	}

	public void clearPigeonSpeedEnabledByJmx() {
		this.pigeonSpeedEnabledSetByJmx = null;
	}

	public void setHeartBeatResponseEnabledByJmx() {
		this.isHeartBeatResponseSetByJmx = true;
	}

	public void setHeartBeatResponseDisabledByJmx() {
		this.isHeartBeatResponseSetByJmx = false;
	}

	public static String getStringValueFromLion(String config, String defaultValue) {
		String configVal = null;
		if (isLionApiValid()) {
			try {
				configVal = ConfigCache.getInstance().getProperty(config);
			} catch (Exception e) {
				logLionError(config);
			}
		}
		return configVal != null ? configVal : defaultValue;
	}

	public static long getLongValueFromLion(String config, long defaultValue) {
		Long configVal = null;
		if (isLionApiValid()) {
			try {
				configVal = ConfigCache.getInstance().getLongProperty(config);
			} catch (Exception e) {
				logLionError(config);
			}
		}
		return configVal != null ? configVal : defaultValue;
	}

	public static int getIntValueFromLion(String config, int defaultValue) {
		Integer configVal = null;
		if (isLionApiValid()) {
			try {
				configVal = ConfigCache.getInstance().getIntProperty(config);
			} catch (Exception e) {
				logLionError(config);
			}
		}
		return configVal != null ? configVal : defaultValue;
	}

	public static boolean getBooleanValueFromLion(String config, boolean defaultValue) {
		Boolean configVal = null;
		if (isLionApiValid()) {
			try {
				configVal = ConfigCache.getInstance().getBooleanProperty(config);
			} catch (Exception e) {
				logLionError(config);
			}
		}
		return configVal != null ? configVal : defaultValue;
	}

	private void checkHawkAndLionStatus() {
		try {
			Class.forName("com.dianping.hawk.jmx.HawkJMXUtil");
			isHawkApiValid = true;
		} catch (ClassNotFoundException e) {
			logger.error("HawkJMXUtil not found in classpath, so pigeon's jmx control is unavailable.");
		} catch (Exception e) {
			logger.error("Reflect HawkJMXUtil failed, so pigeon's jmx control is unavailable.");
		}
		try {
			Class.forName("com.dianping.lion.client.ConfigCache");
			isLionApiValid = true;
		} catch (ClassNotFoundException e) {
			logger.error("ConfigCache not found in classpath, so pigeon's lion control is unavailable.");
		} catch (Exception e) {
			logger.error("Reflect ConfigCache failed, so pigeon's lion control is unavailable.");
		}
	}

	private static void logLionError(String configName) {
		logError("Get config[" + configName + "] from lion failed.", null);
	}

	private static void logError(String message, Exception t) {
		if (errorLogSeed++ % 1000 == 0) {
			if (t != null) {
				logger.warn(message, t);
			} else {
				logger.warn(message);
			}
			errorLogSeed = 0;
		}
	}

}
