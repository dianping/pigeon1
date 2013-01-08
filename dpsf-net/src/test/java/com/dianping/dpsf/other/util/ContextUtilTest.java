/**
 * Project: ${dpsf-net.aid}
 * 
 * File Created at 2012-5-17
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
package com.dianping.dpsf.other.util;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.dianping.avatar.tracker.ExecutionContextHolder;
import com.dianping.avatar.tracker.TrackerContext;
import com.dianping.dpsf.ContextUtil;

/**
 * @author danson.liu
 *
 */
public class ContextUtilTest {

	private static final String SERVICE_NAME = "http://service.dianping.com/echoService/echoService_1.0.0";
	private static final String METHOD_NAME = "echo";
	private static final String HOST = "192.168.8.11";
	private static final int PORT = 3000;

	@Test
	public void testCreateContext() {
		ExecutionContextHolder.setTrackerContext(new TrackerContext());
		Object context = ContextUtil.createContext(SERVICE_NAME, METHOD_NAME, HOST, PORT);
		assertNotNull(context);
		assertTrue(context instanceof TrackerContext);
		TrackerContext contextCreated = (TrackerContext) context;
		assertEquals(SERVICE_NAME + "." + METHOD_NAME + "@" + HOST + ":" + PORT, contextCreated.getLocation());
	}
	
	@Test
	public void testSetContext() {
		Object context = new TrackerContext();
		ContextUtil.setContext(context);
		assertSame(context, ExecutionContextHolder.getTrackerContext());
	}
	
	@Test
	public void testGetContext() {
		TrackerContext trackerContext = new TrackerContext();
		ExecutionContextHolder.setTrackerContext(trackerContext);
		assertSame(trackerContext, ContextUtil.getContext());
	}
	
	@Test
	public void testAddSuccessContext() {
		ExecutionContextHolder.setTrackerContext(new TrackerContext());
		TrackerContext context = new TrackerContext();
		ContextUtil.addSuccessContext(context);
		List<TrackerContext> remoteContexts = ExecutionContextHolder.getTrackerContext().getRemoteContexts();
		assertEquals(1, remoteContexts.size());
		TrackerContext remoteTrackerContext = remoteContexts.get(0);
		assertTrue(remoteTrackerContext.isSucceed());
		assertSame(context, remoteTrackerContext);
	}
	
	@Test
	public void testAddFailedContext() {
		ExecutionContextHolder.setTrackerContext(new TrackerContext());
		TrackerContext context = new TrackerContext();
		ContextUtil.addFailedContext(context);
		List<TrackerContext> remoteContexts = ExecutionContextHolder.getTrackerContext().getRemoteContexts();
		assertEquals(1, remoteContexts.size());
		TrackerContext remoteTrackerContext = remoteContexts.get(0);
		assertFalse(remoteTrackerContext.isSucceed());
		assertSame(context, remoteTrackerContext);
	}
	
	@Test
	public void testGetTooken() {
		TrackerContext context = new TrackerContext();
		String token = "token";
		context.setToken(token);
		String tokenGot = ContextUtil.getToken(context);
		assertEquals(token, tokenGot);
	}
	
	@Test
	public void testGetOrder() {
		TrackerContext context = new TrackerContext();
		Integer order = new Integer(3);
		context.addExtension(ContextUtil.TRAC_ORDER, order);
		Integer orderGot = ContextUtil.getOrder(context);
		assertEquals(order, orderGot);
	}
	
	@Test
	public void testSetOrder() {
		TrackerContext context = new TrackerContext();
		Integer order = new Integer(3);
		ContextUtil.setOrder(context, order);
		Object orderGot = context.getExtension(ContextUtil.TRAC_ORDER);
		assertEquals(order, orderGot);
	}
	
}
