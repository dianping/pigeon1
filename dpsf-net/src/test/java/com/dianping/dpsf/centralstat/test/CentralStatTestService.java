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
 * @author Leo Liang
 * 
 */
public interface CentralStatTestService {

	public String invokeSyncNormal(String name);

	public String invokeSyncServiceException(String name);

	public String invokeSyncTimeOut(String name);

	public String invokeSyncError();

	public String invokeFutureNormal(String name);

	public String invokeFutureServiceException(String name);

	public String invokeFutureTimeOut(String name);

	public String invokeFutureError();
	
	public String invokeOneWayNormal(String name);

	public String invokeOneWayServiceException(String name);

	public String invokeOneWayTimeOut(String name);

	public String invokeOneWayError();
	
	public String invokeCallBackNormal(String name);

	public String invokeCallBackServiceException(String name);

	public String invokeCallBackTimeOut(String name);

	public String invokeCallBackError();

}
