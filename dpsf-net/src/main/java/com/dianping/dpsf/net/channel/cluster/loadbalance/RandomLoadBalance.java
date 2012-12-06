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

/**
 * 随即负载均衡策略
 * @author danson.liu
 *
 */
public class RandomLoadBalance extends AbstractLoadBalance {
	
	public static final String NAME = "random";
	
	public static final LoadBalance instance = new RandomLoadBalance();
	
	@Override
	public Client doSelect(List<Client> clients, DPSFRequest request, int[] weights) {
		int clientSize = clients.size();
		int totalWeight = 0;
		boolean weightAllSame = true;
		for (int i = 0; i < clientSize; i++) {
			totalWeight += weights[i];
			if (weightAllSame && i > 0 && weights[i] != weights[i - 1]) {
				weightAllSame = false;
			}
		}
		if (!weightAllSame) {
			int weightPoint = random.nextInt(totalWeight);
			for (int i = 0; i < clientSize; i++) {
				Client client = clients.get(i);
				weightPoint -= weights[i];
				if (weightPoint < 0) {
					return client;
				}
			}
		}
		return clients.get(random.nextInt(clientSize));
	}

}
