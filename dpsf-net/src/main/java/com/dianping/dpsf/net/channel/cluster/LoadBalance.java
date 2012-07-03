/**
 * Project: ${dpsf-net.aid}
 * 
 * File Created at 2012-3-17
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

import java.util.List;

import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.net.channel.Client;

/**
 * 负载均衡策略接口, 负责分派请求到指定的服务端
 * @author danson.liu
 *
 */
public interface LoadBalance {
	
	Client select(List<Client> clients, DPSFRequest request, WeightAccessor weightAccessor);

}
