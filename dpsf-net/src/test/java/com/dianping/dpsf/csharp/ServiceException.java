package com.dianping.dpsf.csharp;

import java.io.Serializable;

public class ServiceException implements Serializable {
	
	private String exceptionName;
	private String errorMessage;
	private String errorStackTrace;
	
	public ServiceException(String name, String message, String stackTrace) {
		this.exceptionName = name;
		this.errorMessage = message;
		this.errorStackTrace = stackTrace;
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
