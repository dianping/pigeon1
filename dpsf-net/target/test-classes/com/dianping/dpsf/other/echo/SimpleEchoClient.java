/**
 * Project: dpsf-net
 * 
 * File Created at 2011-8-22
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dianping.lion.client.ConfigCache;

public class SimpleEchoClient {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		ConfigCache.getInstance("192.168.7.41:2182");
		ApplicationContext ctx = new ClassPathXmlApplicationContext("echo-client.xml");
		IEcho s = (IEcho) ctx.getBean("echo");
		
		
		while(true) {
			try {
				System.out.println(s.echo("aa"));
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private static List<String[]> generateHostList() {
		List<String[]> hostList = new ArrayList<String[]>();
		hostList.add(new String[]{"127.0.0.1", "20001"});
		hostList.add(new String[]{"127.0.0.1", "20002"});
		hostList.add(new String[]{"127.0.0.1", "20003"});
		
		Random rnd = new Random(System.currentTimeMillis());
		int num = rnd.nextInt(3) + 1;
		Collections.shuffle(hostList);
		return hostList.subList(0, num);
	}

}
