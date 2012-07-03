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

/**
 * TODO Comment of CentralStatTestServiceImpl
 * 
 * @author Leo Liang
 * 
 */
public class CentralStatTestServiceImpl implements CentralStatTestService {

	public String invokeSyncNormal(String name) {
		return name;
	}

	public String invokeSyncServiceException(String name) {
		throw new RuntimeException(name);
	}

	public String invokeSyncTimeOut(String name) {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return name;
	}

	public String invokeSyncError() {
		// TODO Auto-generated method stub
		return null;
	}

	public String invokeFutureNormal(String name) {
		return name;
	}

	public String invokeFutureServiceException(String name) {
		throw new RuntimeException(name);
	}

	public String invokeFutureTimeOut(String name) {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return name;
	}

	public String invokeFutureError() {
		// TODO Auto-generated method stub
		return null;
	}

	public String invokeOneWayNormal(String name) {
		return name;
	}

	public String invokeOneWayServiceException(String name) {
		throw new RuntimeException(name);
	}

	public String invokeOneWayTimeOut(String name) {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return name;
	}

	public String invokeOneWayError() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.centralstat.test.CentralStatTestService#invokeCallBackNormal(java.lang.String)
	 */
	@Override
	public String invokeCallBackNormal(String name) {
		return name;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.centralstat.test.CentralStatTestService#invokeCallBackServiceException(java.lang.String)
	 */
	@Override
	public String invokeCallBackServiceException(String name) {
		throw new RuntimeException(name);
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.centralstat.test.CentralStatTestService#invokeCallBackTimeOut(java.lang.String)
	 */
	@Override
	public String invokeCallBackTimeOut(String name) {
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return name;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.centralstat.test.CentralStatTestService#invokeCallBackError()
	 */
	@Override
	public String invokeCallBackError() {
		// TODO Auto-generated method stub
		return null;
	}
}
