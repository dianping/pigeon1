/**
 * Project: ${dpsf-net.aid}
 * 
 * File Created at 2011-8-18
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
package com.dianping.dpsf.stat;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.DPSFLog;
import com.dianping.hawk.HawkExtend;

/**
 * @author Leo Liang
 * 
 */
public class CentralStatService {
	private static Logger		logger					= DPSFLog.getLogger();
	private static Logger		debugLog				= Logger.getLogger(CentralStatService.class);

	private static final String	HAWKEXTEND_CLASS_NAME	= "com.dianping.hawk.HawkExtend";
	private static Class<?>		hawkExtendClass			= null;

	static {
		try {
			hawkExtendClass = Class.forName(HAWKEXTEND_CLASS_NAME);
		} catch (Exception e) {
			logger.warn("App does not have HawkExtend.");
		}
	}

	public static void notifyMethodInvoke(CentralStatContext context) {
		

		try {
			if (hawkExtendClass != null && context != null) {
				StringBuilder newMethodFullName = new StringBuilder(context.getMethodName());
				newMethodFullName.append("(");
				
				if(context.getParamsType() != null && context.getParamsType().length > 0){
					for(Class<?> type: context.getParamsType()){
						newMethodFullName.append(type.getName()).append(",");
					}
					newMethodFullName = newMethodFullName.deleteCharAt(newMethodFullName.length() - 1);
				}
				newMethodFullName.append(")");
				
				if (debugLog.isDebugEnabled()) {
					debugLog.debug("Debug***************" + context + "methodName:" + newMethodFullName.toString());
				}
				
				if (Constants.CALLTYPE_NOREPLY == context.getCallType()) {
					HawkExtend.logConsumer(context.getServiceName(), newMethodFullName.toString(), context.getProviderIp());
				} else {
					HawkExtend.logConsumer(context.getServiceName(), newMethodFullName.toString(), context.getProviderIp(),
							context.getReturnCode().code, context.getDuration());
				}
			}
		} catch (Throwable e) {
			logger.warn("Notify Stat center failed.");
		}
	}

	public static enum ReturnCode {
		SUCCESS(1), TIMEOUT(-1), EXCEPTION(-2);

		private int	code;

		private ReturnCode(int code) {
			this.code = code;
		}

	}

	public static class CentralStatContext implements Serializable {

		private static final long	serialVersionUID	= 3979449756309021285L;
		private String				serviceName;
		private String				methodName;
		private Class<?>[]			paramsType;
		private String				providerIp;
		private ReturnCode			returnCode;
		private Long				duration;
		private int					callType;

		public CentralStatContext() {

		}

		public CentralStatContext(String serviceName, String methodName, Class<?>[] paramsType, String providerIp, int callType) {
			this.serviceName = serviceName;
			this.methodName = methodName;
			this.providerIp = providerIp;
			this.callType = callType;
			this.paramsType = paramsType;
		}
		
		public CentralStatContext(String serviceName, String methodName, String providerIp, int callType) {
			this.serviceName = serviceName;
			this.methodName = methodName;
			this.providerIp = providerIp;
			this.callType = callType;
		}


		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "CentralStatContext [serviceName=" + serviceName + ", methodName=" + methodName + ", paramsType="
					+ Arrays.toString(paramsType) + ", providerIp=" + providerIp + ", returnCode=" + returnCode
					+ ", duration=" + duration + ", callType=" + callType + "]";
		}

		/**
		 * @return the paramsType
		 */
		public Class<?>[] getParamsType() {
			return paramsType;
		}

		/**
		 * @param paramsType the paramsType to set
		 */
		public void setParamsType(Class<?>[] paramsType) {
			this.paramsType = paramsType;
		}

		/**
		 * @return the serviceName
		 */
		public String getServiceName() {
			return serviceName;
		}

		/**
		 * @param serviceName
		 *            the serviceName to set
		 */
		public void setServiceName(String serviceName) {
			this.serviceName = serviceName;
		}

		/**
		 * @return the methodName
		 */
		public String getMethodName() {
			return methodName;
		}

		/**
		 * @param methodName
		 *            the methodName to set
		 */
		public void setMethodName(String methodName) {
			this.methodName = methodName;
		}

		/**
		 * @return the providerIp
		 */
		public String getProviderIp() {
			return providerIp;
		}

		/**
		 * @param providerIp
		 *            the providerIp to set
		 */
		public void setProviderIp(String providerIp) {
			this.providerIp = providerIp;
		}

		/**
		 * @return the returnCode
		 */
		public ReturnCode getReturnCode() {
			return returnCode;
		}

		/**
		 * @param returnCode
		 *            the returnCode to set
		 */
		public void setReturnCode(ReturnCode returnCode) {
			this.returnCode = returnCode;
		}

		/**
		 * @return the duration
		 */
		public Long getDuration() {
			return duration;
		}

		/**
		 * @param duration
		 *            the duration to set
		 */
		public void setDuration(Long duration) {
			this.duration = duration;
		}

		/**
		 * @return the callType
		 */
		public int getCallType() {
			return callType;
		}

		/**
		 * @param callType
		 *            the callType to set
		 */
		public void setCallType(int callType) {
			this.callType = callType;
		}

	}
}
