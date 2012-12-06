/**
 * Project: ${dpsf-net.aid}
 * 
 * File Created at 2012-3-27
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

import java.util.Map;

/**
 * TODO Comment of ServiceInvokeStatsResult
 * @author danson.liu
 *
 */
public class ServiceInvokeStatsResult {

	private Map<String, ServiceInvokeStats> stats;
	
	private int total;

	public Map<String, ServiceInvokeStats> getStats() {
		return stats;
	}

	public void setStats(Map<String, ServiceInvokeStats> stats) {
		this.stats = stats;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
	
}
