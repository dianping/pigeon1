/**
 * Project: ${dpsf-net.aid}
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

import java.util.ArrayList;
import java.util.List;

/**
 * 将lion推送的动态服务信息发送到感兴趣的listener
 * @author marsqing
 *
 */
public class LionNotifier {

	private static List<ServiceProviderChangeListener> listeners = new ArrayList<ServiceProviderChangeListener>();

	public synchronized static void addListener(ServiceProviderChangeListener listener) {
		listeners.add(listener);
	}

	public static void providerRemoved(String serviceName, String host, int port) {
		for (ServiceProviderChangeListener listener : listeners) {
			listener.providerRemoved(new ServiceProviderChangeEvent(serviceName, host, port, -1));
		}
	}

	public static void providerAdded(String serviceName, String host, int port, int weight) {
		for (ServiceProviderChangeListener listener : listeners) {
			listener.providerAdded(new ServiceProviderChangeEvent(serviceName, host, port, weight));
		}
	}

	public static void hostWeightChanged(String host, int port, int weight) {
		for (ServiceProviderChangeListener listener : listeners) {
			listener.hostWeightChanged(new ServiceProviderChangeEvent(null, host, port, weight));
		}
	}
	
}
