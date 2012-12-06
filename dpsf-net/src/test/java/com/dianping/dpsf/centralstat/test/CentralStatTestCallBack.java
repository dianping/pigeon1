/**
 * Project: ${dpsf-net.aid}
 * 
 * File Created at 2011-8-23
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

import com.dianping.dpsf.async.ServiceCallback;
import com.dianping.dpsf.exception.DPSFException;

/**
 * TODO Comment of CentralStatTestCallBack
 * @author Leo Liang
 *
 */
public class CentralStatTestCallBack implements ServiceCallback {

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.async.ServiceCallback#callback(java.lang.Object)
	 */
	@Override
	public void callback(Object result) {
		System.out.println("Call back: " + result);
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.async.ServiceCallback#serviceException(java.lang.Exception)
	 */
	@Override
	public void serviceException(Exception e) {
		e.printStackTrace();
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.async.ServiceCallback#frameworkException(com.dianping.dpsf.exception.DPSFException)
	 */
	@Override
	public void frameworkException(DPSFException e) {
		e.printStackTrace();
	}

}
