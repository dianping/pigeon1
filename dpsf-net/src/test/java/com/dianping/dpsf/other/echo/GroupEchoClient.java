/**
 * Project: dpsf-net
 * 
 * File Created at 2011-8-18
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

import java.util.HashSet;
import java.util.Set;

import com.dianping.dpsf.api.ProxyFactory;

public class GroupEchoClient {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		ProxyFactory<IEcho> pf = makePF("xx", "127.0.0.1:20001,127.0.0.1:20002", "1,1");
		ProxyFactory<IEcho> pf2 = makePF("xx2", "127.0.0.1:20003", "1");
		for (int i = 0; i < 10; i++) {
			pf.getProxy().echo("a");
		}
		System.out.println("===============disabling==================");
		Set<String> connectSet = new HashSet<String>();
		connectSet.add("127.0.0.1:20001");
		pf.setGroupRoute("http://service.dianping.com/echoService", "xx", connectSet );
		for (int i = 0; i < 10; i++) {
			pf.getProxy().echo("a");
		}
		System.out.println("================pf2===================");
		for (int i = 0; i < 10; i++) {
			pf2.getProxy().echo("a");
		}
	}

	private static ProxyFactory<IEcho> makePF(String group, String hosts, String weight) throws Exception {
		ProxyFactory<IEcho> pf = new ProxyFactory<IEcho>();
		pf.setGroup(group);
		pf.setHosts(hosts);
		pf.setIface(IEcho.class);
		pf.setServiceName("http://service.dianping.com/echoService");
		pf.setTimeout(3000);
		pf.setWeight(weight);
		pf.init();
		return pf;
	}

}
