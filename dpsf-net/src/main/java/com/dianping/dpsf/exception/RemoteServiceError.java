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
package com.dianping.dpsf.exception;

import java.io.Serializable;

/**
 * RemoteServiceException POJO to heterogeneous system
 * @author danson.liu
 *
 */
@SuppressWarnings("serial")
public class RemoteServiceError implements Serializable {

	private String exceptionName;
	
	private String errorMessage;
	
	private String errorStackTrace;
	
	public RemoteServiceError() {
	}

	public RemoteServiceError(String exceptionName, String errorMessage, String errorStackTrace) {
		this.exceptionName = exceptionName;
		this.errorMessage = errorMessage;
		this.errorStackTrace = errorStackTrace;
	}

	public String getExceptionName() {
		return exceptionName;
	}

	public void setExceptionName(String exceptionName) {
		this.exceptionName = exceptionName;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorStackTrace() {
		return errorStackTrace;
	}

	public void setErrorStackTrace(String errorStackTrace) {
		this.errorStackTrace = errorStackTrace;
	}
	
}
