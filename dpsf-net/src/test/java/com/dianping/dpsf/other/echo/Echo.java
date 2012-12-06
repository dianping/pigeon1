package com.dianping.dpsf.other.echo;

/**
 * Project: ${dpsf-net.aid}
 * 
 * File Created at 2011-8-12
 * $Id$
 * 
 * Copyright 2011 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */

public class Echo implements IEcho{

	@Override
	public String echo(String input) {
		return "server" + input;
	}

}
