/**
 * Project: dpsf-net
 * 
 * File Created at 2011-8-24
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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.api.ProxyFactory;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.net.channel.Client;
import com.dianping.dpsf.net.channel.manager.ClientManagerFactory;
import com.dianping.dpsf.other.echo.EchoServer;
import com.dianping.dpsf.other.echo.EchoServer2;
import com.dianping.dpsf.other.echo.IEcho;
import com.dianping.dpsf.protocol.DefaultRequest;


public class ManualRouteTest {

	private static final int CNT = 20;

	static String SN = "http://service.dianping.com/echoService";
	static String SNV2 = "http://service.dianping.com/echoServiceV2";
	
	static DPSFRequest request;

	static String add1 = "127.0.0.1:19999";
	static String add2 = "127.0.0.1:19998";
	static String add3 = "127.0.0.1:19997";
	static String add4 = "127.0.0.1:19996";
	
	@BeforeClass
	public static void setMock() throws Exception {
		EchoServer.main(new String[]{"a"});
		EchoServer2.main(new String[]{"a"});
		request = new DefaultRequest(SN, "", null, Constants.SERILIZABLE_HESSIAN, Constants.MESSAGE_TYPE_SERVICE, 1000, null);
	}

	private static ProxyFactory createProxyFactory(String group, String hosts, Class iface) throws Exception {
		ProxyFactory<IEcho> pf = new ProxyFactory<IEcho>();
		pf.setServiceName(SN);
		pf.setGroup(group);
		pf.setHosts(hosts);
		pf.setIface(iface);
		Method init = pf.getClass().getDeclaredMethod("init", new Class[0]);
		init.setAccessible(true);
		init.invoke(pf, null);
		return pf;
	}
	
	@Ignore
	public void testManualRoute() throws Exception {
		ProxyFactory<IEcho> f1 = createProxyFactory("group1", add1+","+add2, IEcho.class);
		int cnt1 = 0;
		int cnt2 = 0;
		for (int i = 0; i < CNT; i++) {
			Client client = ClientManagerFactory.getClientManager().getClient(SN, "group1", request);
			if(add1.equals(client.getAddress())){
				cnt1++;
			} else {
				cnt2++;
			}
		}
		assertTrue(cnt1 > 0);
		assertTrue(cnt2 > 0);
		
		
		cnt1 = 0;
		cnt2 = 0;
		ProxyFactory<IEcho> f2 = createProxyFactory("group2", add1+","+add2, IEcho.class);
		for (int i = 0; i < CNT; i++) {
			Client client = ClientManagerFactory.getClientManager().getClient(SN, "group2", request);
			if(add1.equals(client.getAddress())){
				cnt1++;
			} else {
				cnt2++;
			}
		}
		assertTrue(cnt1 > 0);
		assertTrue(cnt2 > 0);
		
		
		cnt1 = 0;
		cnt2 = 0;
		Set<String> add1Set = new HashSet<String>();
		add1Set.add(add1);
		f2.setGroupRoute(SN, "group2", add1Set );
		for (int i = 0; i < CNT; i++) {
			Client client = ClientManagerFactory.getClientManager().getClient(SN, "group2", request);
			if(add1.equals(client.getAddress())){
				cnt1++;
			} else {
				cnt2++;
			}
		}
		assertEquals(CNT, cnt1);
		assertEquals(0, cnt2);
		
		cnt1 = 0;
		cnt2 = 0;
		for (int i = 0; i < CNT; i++) {
			Client client = ClientManagerFactory.getClientManager().getClient(SN, "group1", request);
			if(add1.equals(client.getAddress())){
				cnt1++;
			} else {
				cnt2++;
			}
		}
		assertTrue(cnt1 > 0);
		assertTrue(cnt2 > 0);
		
	}
	
}
