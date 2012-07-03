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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.net.channel.Client;
import com.dianping.dpsf.net.channel.manager.ClientManagerFactory;
import com.dianping.dpsf.net.channel.netty.NettyClientManager;
import com.dianping.dpsf.other.echo.EchoServer;
import com.dianping.dpsf.other.echo.EchoServer2;
import com.dianping.dpsf.other.echo.EchoServer3;
import com.dianping.dpsf.other.echo.IEcho;
import com.dianping.dpsf.other.echo.IEchoV2;
import com.dianping.dpsf.protocol.DefaultRequest;
import com.dianping.dpsf.spring.ProxyBeanFactory;


public class RouteTest {
	
	private static final int CNT = 20;

	static String SN = "http://service.dianping.com/echoService";
	static String SNV2 = "http://service.dianping.com/echoServiceV2";
	
	static DPSFRequest request;

	static String add1 = "127.0.0.1:19999";
	static String add2 = "127.0.0.1:19998";
	static String add3 = "127.0.0.1:19997";
	static String add4 = "127.0.0.1:19996";
	
	static ProxyBeanFactory f;
	
	@BeforeClass
	public static void setMock() throws Exception {
		EchoServer.main(new String[]{"a"});
		EchoServer2.main(new String[]{"a"});
		EchoServer3.main(new String[]{"a"});
		
		System.setProperty(NettyClientManager.LION_CLIENT_CLASS, PigeonClientMock.class.getName());
		PigeonClientMock.setServiceAddress(SN, add1);
		
		f = createProxyBeanFactory(SN, IEcho.class.getName());
		
		request = new DefaultRequest(SN, "", null, Constants.SERILIZABLE_HESSIAN, Constants.MESSAGE_TYPE_SERVICE, 1000, null);
	}

	private static ProxyBeanFactory createProxyBeanFactory(String sn, String iface) throws Exception {
		return createProxyBeanFactory(sn, iface, null);
	}
	
	private static ProxyBeanFactory createProxyBeanFactory(String sn, String iface, String hosts) throws Exception {
		ProxyBeanFactory pf = new ProxyBeanFactory();
		pf.setIface(iface);
		pf.setServiceName(sn);
		pf.setHosts(hosts);
		pf.setIsTest(true);
		pf.setGroup(IEcho.class.getName() + "_1");
		Method init = pf.getClass().getDeclaredMethod("init", new Class[0]);
		init.setAccessible(true);
		init.invoke(pf, null);
		return pf;
	}
	
	@Before
	public void initPF() throws Exception {
	}
	
	private void setHostList(String ... adds) {
		List<String[]> hostList = new ArrayList<String[]>();
		for (int i = 0; i < adds.length; i++) {
			hostList.add(adds[i].split(":"));
		}
		PigeonClientMock.getSc().onServiceHostChange(SN, hostList);
	}
	
	private int[] runAndReturnHitCount(String...adds) throws Exception {
		int[] cnts = new int[adds.length];
		List addList = Arrays.asList(adds);
		setHostList(adds);
		for (int i = 0; i < CNT; i++) {
			Client client = ClientManagerFactory.getClientManager().getClient(SN, IEcho.class.getName() + "_1", request);
			cnts[addList.indexOf(client.getAddress())]++;
			((IEcho)f.getObject()).echo("aa");
		}
		return cnts;
	}
	
	@Test
	public void testAddSingleConnect() throws Exception {
		
		Client client = ClientManagerFactory.getClientManager().getClient(SN, IEcho.class.getName() + "_1", request);
		assertEquals(add1, client.getAddress());
		
		IEcho ie = (IEcho) f.getObject();
		ie.echo("aa");
		
	}
	
	@Test
	public void testAddMultipeConnect() throws Exception {
		
		int[] cnts = runAndReturnHitCount(add1, add2);
		
		
		for (int i = 0; i < cnts.length; i++) {
			assertTrue(cnts[i] > 0);
		}
		
		cnts = runAndReturnHitCount(add3);
		assertEquals(CNT, cnts[0]);
		
	}
	
	@Test
	public void testSetWeight() throws Exception {
		PigeonClientMock.getSc().onHostWeightChange(add1, 0);
		int[] cnts = runAndReturnHitCount(add1, add2);
		assertEquals(CNT, cnts[1]);
		assertEquals(0, cnts[0]);
		
		PigeonClientMock.getSc().onHostWeightChange(add1, 1);
		cnts = runAndReturnHitCount(add1, add2);
		for (int i = 0; i < cnts.length; i++) {
			assertTrue(cnts[i] > 0);
		}
	}
	
	@Test
	public void testNoRoute() throws Exception {
		try{
			runAndReturnHitCount();
			fail();
		} catch (Exception e) {
		}
		
		int[] cnts = runAndReturnHitCount(add1);
		assertEquals(CNT, cnts[0]);
	}
	
	@Test
	public void testAllZeroWeight() throws Exception {
		PigeonClientMock.getSc().onHostWeightChange(add1, 0);
		PigeonClientMock.getSc().onHostWeightChange(add2, 0);
		PigeonClientMock.getSc().onHostWeightChange(add3, 0);
		
		try {
			runAndReturnHitCount(add1, add2, add3);
			fail();
		} catch (Exception e) {
		}
		
		PigeonClientMock.getSc().onHostWeightChange(add1, 1);
		PigeonClientMock.getSc().onHostWeightChange(add2, 0);
		PigeonClientMock.getSc().onHostWeightChange(add3, 1);
		
		int[] cnts = runAndReturnHitCount(add1, add2, add3);
		assertTrue(cnts[0] > 0);
		assertEquals(0, cnts[1]);
		assertTrue(cnts[2] > 0);
		
	}
	
	@Test
	public void testAddUnreachableConnect() throws Exception {
		int[] cnts = runAndReturnHitCount(add1, add2, add3, add4);
	}
	
	@Test
	public void testMultiServiceOnSingleConnect() throws Exception {
		
		ProxyBeanFactory f2 = createProxyBeanFactory(SNV2, IEchoV2.class.getName(), "127.0.0.1:19998");
		
		((IEchoV2)f2.getObject()).echoV2("aa");
		
		setHostList(add1);
		
		((IEchoV2)f2.getObject()).echoV2("aa");
		
	}
	
	
}
