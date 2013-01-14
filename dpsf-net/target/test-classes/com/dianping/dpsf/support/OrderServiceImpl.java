/**
 * Project: ${dpsf-net.aid}
 * 
 * File Created at 2012-5-15
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
package com.dianping.dpsf.support;

import com.dianping.dpsf.protocol.protobuf.DpsfTestMessages.CreateOrderRequest;
import com.dianping.dpsf.protocol.protobuf.DpsfTestMessages.CreateOrderResponse;
import com.dianping.dpsf.protocol.protobuf.DpsfTestMessages.Order;
import com.dianping.dpsf.protocol.protobuf.DpsfTestMessages.OrderID;
import com.dianping.dpsf.protocol.protobuf.DpsfTestMessages.OrderService.BlockingInterface;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;

/**
 * @author danson.liu
 *
 */
public class OrderServiceImpl implements BlockingInterface {

	@Override
	public CreateOrderResponse createOrder(RpcController controller, CreateOrderRequest request) 
		throws ServiceException {
		return CreateOrderResponse.newBuilder().setSucceed(true).build();
	}

	@Override
	public Order getOrder(RpcController controller, OrderID request) throws ServiceException {
		throw new ServiceException("Order with id[" + request.getId() + "] not found.");
	}

}
