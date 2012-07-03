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
import java.util.Random;

import org.apache.log4j.Logger;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.context.ClientContext;
import com.dianping.dpsf.exception.NoConnectionException;
import com.dianping.dpsf.net.channel.Client;
import com.dianping.dpsf.net.channel.cluster.LoadBalance;
import com.dianping.dpsf.net.channel.cluster.WeightAccessor;

/**
 * AbstractLoadBalance
 * @author danson.liu
 *
 */
public abstract class AbstractLoadBalance implements LoadBalance {
	
	private static Logger logger = DPSFLog.getLogger();
	
	protected Random random = new Random();

	@Override
	public Client select(List<Client> clients, DPSFRequest request, WeightAccessor weightAccessor) {
		if (clients == null || clients.isEmpty()) {
			return null;
		}
		Client selectedClient = null;
		String forceAddress = ClientContext.getUseClientAddress();
		if (forceAddress != null && forceAddress.length() > 0) {
			//客户端强制路由
			for (Client client : clients) {
				if (forceAddress.equals(client.getAddress())) {
					selectedClient = client;
					break;
				}
			}
			if (selectedClient == null) {
				throw new NoConnectionException("Force used server[" + forceAddress + "] is not connected for service[" + request.getServiceName() + "].");
			}
		} else {
			if (clients.size() == 1) {
				selectedClient = clients.get(0);
			} else {
				try {
					selectedClient = doSelect(clients, request, getWeights(clients, request.getServiceName(), weightAccessor));
				} catch (Throwable e) {
					logger.warn("Failed to do load balance[" + getClass().getName() + "], detail: " + e.getMessage() + ", use random instead.");
					selectedClient = clients.get(random.nextInt(clients.size()));
				}
			}
		}
		if (selectedClient != null) {
			int weight = weightAccessor.getWeightWithDefault(request.getServiceName(), selectedClient.getAddress());
			request.setAttachment(Constants.REQ_ATTACH_FLOW, 1.0f / (weight > 0 ? weight : 1));
		}
		return selectedClient;
	}

	/**
	 * [w1, w2, w3, maxWeightIndex]
	 * @param clients
	 * @param serviceName
	 * @param weightAccessor
	 * @return
	 */
	private int[] getWeights(List<Client> clients, String serviceName, WeightAccessor weightAccessor) {
		int clientSize = clients.size();
		int[] weights = new int[clientSize + 1];
		int maxWeightIdx = 0;
		int maxWeight = Integer.MIN_VALUE;
		for (int i = 0; i < clientSize; i++) {
			weights[i] = weightAccessor.getWeightWithDefault(serviceName, clients.get(i).getAddress());
			if (weights[i] > maxWeight) {
				maxWeight = weights[i];
				maxWeightIdx = i;
			}
		}
		weights[clientSize] = maxWeightIdx;
		return weights;
	}

	protected abstract Client doSelect(List<Client> clients, DPSFRequest request, int[] weights);

}
