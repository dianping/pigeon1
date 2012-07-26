/**
 * Project: dpsf-net
 * 
 * File Created at 2011-8-23
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
package com.dianping.dpsf.other.route;

import java.util.HashMap;
import java.util.Map;

import com.dianping.lion.pigeon.PigeonClient;
import com.dianping.lion.pigeon.ServiceChange;

public class PigeonClientMock implements PigeonClient {

	static ServiceChange sc;

	public PigeonClientMock(ServiceChange sc) {
		PigeonClientMock.sc = sc;
	}

	public static ServiceChange getSc() {
		return sc;
	}

	static Map<String, Integer> connectToWeight = new HashMap<String, Integer>();
	static Map<String, String> serviceNameToAddressList = new HashMap<String, String>();

	public static void setHostWeight(String connect, int weight) {
		connectToWeight.put(connect, weight);
	}

	public static void setServiceAddress(String sn, String addr) {
		serviceNameToAddressList.put(sn, addr);
	}

	@Override
	public int getHostWeigth(String hostAndPort) throws Exception {
		Integer wt = connectToWeight.get(hostAndPort);
		return wt == null ? 1 : wt;
	}

	@Override
	public String getServiceAddress(String serviceName) throws Exception {
		return serviceNameToAddressList.get(serviceName);
	}

}
