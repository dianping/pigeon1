/**
 * Project: ${dpsf-net.aid}
 * 
 * File Created at 2012-3-25
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * ServiceInvokeStat
 * @author danson.liu
 *
 */
public class ServiceInvokeStats implements Serializable {
	
	private static final long serialVersionUID = -8163705687860260256L;
	
	private int index;
	private Map<String, ServiceInvokeStat> connect2Stats = new HashMap<String, ServiceInvokeStat>();
	
	public ServiceInvokeStats() {
	}

	public ServiceInvokeStats(int index) {
		this.index = index;
	}

	public void addServiceStat(ServiceInvokeStat stat) {
		this.connect2Stats.put(stat.getConnect(), stat);
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}
