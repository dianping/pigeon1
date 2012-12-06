/**
 * Project: dpsf-net
 * 
 * File Created at 2011-8-17
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

import com.dianping.lion.pigeon.PigeonClient;
import com.dianping.lion.pigeon.ServiceChange;

public class PigeonClientMock implements PigeonClient {

	static ServiceChange sc;
	static String serviceAddress;

	public PigeonClientMock(ServiceChange sc) {
		PigeonClientMock.sc = sc;
	}

	public static String getServiceAddress() {
		return serviceAddress;
	}

	public static void setServiceAddress(String serviceAddress) {
		PigeonClientMock.serviceAddress = serviceAddress;
	}

	public void initZKWatch(ServiceChange serviceChange) throws Exception {
		sc = serviceChange;
	}

	public static ServiceChange getSc() {
		return sc;
	}

	public int getWeight(String host) {
		return 1;
	}

	@Override
	public int getHostWeigth(String hostName) throws Exception {
		return 1;
	}

	@Override
	public String getServiceAddress(String serviceName) throws Exception {
		return serviceAddress;
	}

}
