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
package com.dianping.dpsf.jmx;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dianping.hawk.jmx.HawkJMXUtil;

/**
 * @author danson.liu
 *
 */
public class ManagementContext {
	
	private static Log logger = LogFactory.getLog(ManagementContext.class);
	
	private static volatile ManagementContext instance = null;
	private static Object monitor = new Object();
	
	private Set<Object> registeredMBeans = Collections.synchronizedSet(new HashSet<Object>());
	
	private Class<?> hawkJMXUtilClass; 
	
	public static ManagementContext getInstance() {
		if (instance == null) {
			synchronized (monitor) {
				if (instance == null) {
					instance = new ManagementContext();
				}
			}
		}
		return instance;
	}
	
	private ManagementContext() {
		try {
			hawkJMXUtilClass = Class.forName("com.dianping.hawk.jmx.HawkJMXUtil");
		} catch (ClassNotFoundException e) {
			logger.warn("HawkJMXUtil not found in classpath, so jmx minitor function is unavailable.");
		}
	}
	
	public void registerMBean(Object bean) {
		if (hawkJMXUtilClass != null && !registeredMBeans.contains(bean)) {
			HawkJMXUtil.registerMBean(bean);
			registeredMBeans.add(bean);
		}
	}
	
}
