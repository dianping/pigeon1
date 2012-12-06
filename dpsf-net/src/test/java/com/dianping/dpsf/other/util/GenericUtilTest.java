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

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.dianping.dpsf.LocalIP;
import com.dianping.dpsf.exception.ExceptionUtil;
import com.dianping.dpsf.exception.NetException;

/**
 * @author danson.liu
 *
 */
public class GenericUtilTest {

	@Test
	public void testLocalIpUtilGetAddress() {
		assertTrue(LocalIP.getAddress().startsWith("192.168."));
	}
	
	@Test
	public void testExceptionUtil() {
		NetException ne = new NetException();
		ExceptionUtil.addException(ne);
		NetException exceptionGot = ExceptionUtil.get();
		assertSame(ne, exceptionGot);
	}
	
}
