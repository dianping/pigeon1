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
package com.dianping.dpsf.net.channel.cluster.loadbalance;

import java.util.List;

import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.net.channel.Client;
import com.dianping.dpsf.net.channel.cluster.LoadBalance;
import com.dianping.dpsf.stat.RpcStatsPool;

/**
 * 感知服务端负载情况, 将请求路由到负载较低的服务端
 * @author danson.liu
 *
 */
public class LoadAutoawareLoadBalance extends AbstractLoadBalance {
	
	public static final String NAME = "autoaware";
	
	public static final LoadBalance instance = new LoadAutoawareLoadBalance();
	
	@Override
	public Client doSelect(List<Client> clients, DPSFRequest request, int[] weights) {
		float minCapacity = Float.MAX_VALUE;
		int clientSize = clients.size();
		Client[] candidates = new Client[clientSize];
		int candidateIdx = 0;
		for (int i = 0; i < clientSize; i++) {
			Client client = clients.get(i);
			float capacity = RpcStatsPool.getCapacity(client.getAddress());
			if (capacity < minCapacity) {
				minCapacity = capacity;
				candidateIdx = 0;
				candidates[candidateIdx++] = client;
			} else if (capacity == minCapacity) {
				candidates[candidateIdx++] = client;
			}
		}
		return candidateIdx == 1 ? candidates[0] : candidates[random.nextInt(candidateIdx)];
	}

}
