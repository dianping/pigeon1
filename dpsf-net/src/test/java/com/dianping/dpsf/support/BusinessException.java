/**
 * Project: ${dpsf-net.aid}
 * 
 * File Created at 2012-5-17
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

/**
 * @author danson.liu
 * 
 */
public class BusinessException extends Exception {

	private static final long serialVersionUID = -2282139363685173051L;

	public BusinessException(final String message) {
		super(message);
	}

}
