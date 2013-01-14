/**
 * Project: dpsf-net
 * 
 * File Created at 2011-8-19
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
package com.dianping.dpsf.other.echo;

public class EchoV2 implements IEchoV2 {

	@Override
	public String echoV2(String input) {
		return "v2" + input;
	}

}
