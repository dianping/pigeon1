/**
 * Project: ${dpsf-net.aid}
 * 
 * File Created at 2012-6-3
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
package com.dianping.dpsf.csharp;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;

import com.dianping.dpsf.spring.ServiceRegistry;

/**
 * TODO Comment of ServiceToCSharpTest
 * @author danson.liu
 *
 */
@Ignore
public class ServiceToCSharpTest {
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		ServiceRegistry serviceRegistry = new ServiceRegistry();
		serviceRegistry.setPort(2000);
		Map<String, Object> services = new HashMap<String, Object>();
		OrderService demoService = new OrderServiceImpl();
		services.put("http://service.dianping.com/echoService/orderService_1.0.0", demoService);
		serviceRegistry.setServices(services);
		serviceRegistry.init();
		System.out.println("Server started.");
		System.in.read();
	}

}
