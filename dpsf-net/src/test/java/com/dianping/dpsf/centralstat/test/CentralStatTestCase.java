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
package com.dianping.dpsf.centralstat.test;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dianping.dpsf.async.ServiceFutureFactory;
import com.dianping.dpsf.component.impl.DefaultInvoker;
import com.dianping.dpsf.net.channel.manager.ClientManagerFactory;
import com.dianping.dpsf.net.channel.netty.NettyClientManager;

/**
 * @author Leo Liang
 * 
 */
public class CentralStatTestCase {
	private static Logger				log	= Logger.getLogger(CentralStatTestCase.class);
	private static ApplicationContext	context;

	@BeforeClass
	public static void init() throws Exception {
		System.setProperty(NettyClientManager.DISABLE_DYNAMIC_SERVICE, "true");
		new ClassPathXmlApplicationContext("classpath*:centralstat-sever.xml");
		context = new ClassPathXmlApplicationContext("classpath*:centralstat-client.xml");
	}
	

	@AfterClass
	public static void destroy() throws Exception {
		System.setProperty(NettyClientManager.DISABLE_DYNAMIC_SERVICE, "false");
		DefaultInvoker.setInvoker(null);
		ClientManagerFactory.setManager(null);
	}

	@Test
	public void testSyncNormal() {
		CentralStatTestService service = (CentralStatTestService) context.getBean("centralStatTestServiceSync");

		String ret = service.invokeSyncNormal("testSyncNormal");
		Assert.assertEquals("testSyncNormal", ret);
	}

	@Test
	public void testSyncTimeout() {
		log.info("$$$$$$$$$$testSyncTimeout");
		try {
			CentralStatTestService service = (CentralStatTestService) context.getBean("centralStatTestServiceSync");
			service.invokeSyncTimeOut("testSyncTimeout");
		} catch (Exception e) {
		}
	}

	@Test
	public void testSyncError() {
		log.info("$$$$$$$$$$testSyncError");
		try {
			CentralStatTestService service = (CentralStatTestService) context
					.getBean("centralStatTestServiceSyncError");
			service.invokeSyncError();

		} catch (Exception e) {

		}
	}

	@Test
	public void testFutureNormal() throws Throwable {
		log.info("$$$$$$$$$$testFutureNormal");
		CentralStatTestService service = (CentralStatTestService) context.getBean("centralStatTestServiceFuture");
		String ret = service.invokeFutureNormal("testFutureNormal");
		ret = (String) ServiceFutureFactory.getFuture()._get();
		Assert.assertEquals("testFutureNormal", ret);
	}

	@Test
	public void testFutureTimeout() throws Exception {
		log.info("$$$$$$$$$$testFutureTimeout");
		String ret = null;
		try {
			CentralStatTestService service = (CentralStatTestService) context.getBean("centralStatTestServiceFuture");
			service.invokeFutureTimeOut("testFutureTimeout");
			ret = (String) ServiceFutureFactory.getFuture()._get();
			Assert.fail();
		} catch (Throwable e) {
			Assert.assertNull(ret);
		}

	}

	@Test
	public void testFutureError() {
		log.info("$$$$$$$$$$testFutureError");
		try {
			CentralStatTestService service = (CentralStatTestService) context
					.getBean("centralStatTestServiceFutureError");
			service.invokeFutureError();
			Thread.sleep(4000);
			ServiceFutureFactory.getFuture()._get();
		} catch (Throwable e) {

		}
	}

	@Test
	public void testOneWayNormal() throws Exception {
		log.info("$$$$$$$$$$testOneWayNormal");
		CentralStatTestService service = (CentralStatTestService) context.getBean("centralStatTestServiceOneWay");
		service.invokeOneWayNormal("testOneWayNormal");
	}

	@Test
	public void testOneWayTimeout() throws Exception {
		log.info("$$$$$$$$$$testOneWayTimeout");
		String ret = null;
		try {
			CentralStatTestService service = (CentralStatTestService) context.getBean("centralStatTestServiceOneWay");
			service.invokeOneWayTimeOut("testOneWayTimeout");
		} catch (Exception e) {
			Assert.assertNull(ret);
		}

	}

	@Test
	public void testOneWayError() {
		log.info("$$$$$$$$$$testOneWayError");
		try {
			CentralStatTestService service = (CentralStatTestService) context
					.getBean("centralStatTestServiceOneWayError");
			service.invokeOneWayError();
		} catch (Exception e) {

		}
	}

	@Test
	public void testCallBackNormal() throws Exception {
		log.info("$$$$$$$$$$testCallBackNormal");
		CentralStatTestService service = (CentralStatTestService) context.getBean("centralStatTestServiceCallBack");
		String ret = service.invokeCallBackNormal("testCallBackNormal");
	}

	@Test
	public void testCallBackTimeout() throws Exception {
		log.info("$$$$$$$$$$testCallBackTimeout");
		String ret = null;
		try {
			CentralStatTestService service = (CentralStatTestService) context.getBean("centralStatTestServiceCallBack");
			service.invokeCallBackTimeOut("testCallBackTimeout");
		} catch (Exception e) {
			Assert.assertNull(ret);
		}

	}

	@Test
	public void testCallBackError() {
		log.info("$$$$$$$$$$testCallBackError");
		try {
			CentralStatTestService service = (CentralStatTestService) context
					.getBean("centralStatTestServiceCallBackError");
			service.invokeCallBackError();
			Thread.sleep(4000);
		} catch (Exception e) {

		}
	}

	@Test
	public void testSyncServiceException() {
		log.info("$$$$$$$$$$testSyncServiceException");
		try {
			CentralStatTestService service = (CentralStatTestService) context.getBean("centralStatTestServiceSync");
			service.invokeSyncServiceException("testSyncServiceException");
			Assert.fail();
		} catch (Exception e) {
		}

	}
	
	@Test
	public void testFutureServiceException() {
		log.info("$$$$$$$$$$testFutureServiceException");
		try {
			CentralStatTestService service = (CentralStatTestService) context.getBean("centralStatTestServiceFuture");
			service.invokeFutureServiceException("testFutureServiceException");
			ServiceFutureFactory.getFuture()._get();
			Assert.fail();
		} catch (Throwable e) {
		}

	}
	
	@Test
	public void testOneWayServiceException() {
		log.info("$$$$$$$$$$testOneWayServiceException");
		try {
			CentralStatTestService service = (CentralStatTestService) context.getBean("centralStatTestServiceOneWay");
			service.invokeOneWayServiceException("testOneWayServiceException");
		} catch (Exception e) {
		}

	}
	
	@Test
	public void testCallBackServiceException() {
		log.info("$$$$$$$$$$testCallBackServiceException");
		try {
			CentralStatTestService service = (CentralStatTestService) context.getBean("centralStatTestServiceCallBack");
			service.invokeCallBackServiceException("testCallBackServiceException");
			Thread.sleep(3000);
		} catch (Exception e) {
		}

	}

}
