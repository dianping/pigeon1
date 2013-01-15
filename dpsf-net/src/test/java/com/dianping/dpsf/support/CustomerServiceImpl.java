/**
 * Project: ${dpsf-net.aid}
 * 
 * File Created at 2012-5-16
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

import org.apache.thrift.TException;

import com.dianping.dpsf.protocol.thrift.Customer;
import com.dianping.dpsf.protocol.thrift.ServiceException;
import com.dianping.dpsf.protocol.thrift.CustomerService.Iface;

/**
 * TODO Comment of CustomerServiceImpl
 * @author danson.liu
 *
 */
public class CustomerServiceImpl implements Iface {

	@Override
	public Customer getCustomer(int customerId) throws ServiceException, TException {
		return new Customer("pigeon", customerId);
	}

	@Override
	public void createCustomer(Customer customer) throws ServiceException, TException {
		if (!customer.isSetName()) {
			throw new ServiceException(20, "name field not set.");
		}
	}

}
