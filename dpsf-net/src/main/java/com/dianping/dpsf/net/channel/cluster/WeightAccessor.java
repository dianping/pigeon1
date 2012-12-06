/**
 * Project: ${dpsf-net.aid}
 * 
 * File Created at 2012-3-18
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
package com.dianping.dpsf.net.channel.cluster;

/**
 * TODO Comment of WeightAccessor
 * @author danson.liu
 *
 */
public interface WeightAccessor {
	
	Integer getWeight(String serviceName, String address);
	
	int getWeightWithDefault(String serviceName, String address);

}
