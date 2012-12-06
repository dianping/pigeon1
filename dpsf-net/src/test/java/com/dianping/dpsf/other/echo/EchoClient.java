package com.dianping.dpsf.other.echo;

/**
 * Project: ${dpsf-example.aid}
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


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dianping.dpsf.net.channel.manager.PigeonClientMock;

public class EchoClient {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		PigeonClientMock.setServiceAddress("127.0.0.1:20001,127.0.0.1:20002");
		ApplicationContext ctx = new ClassPathXmlApplicationContext("echo-client.xml");
		IEcho s = (IEcho) ctx.getBean("echo");
//		System.out.println(s.echo("aa"));
//		Thread.sleep(3000);
//		System.out.println(s.echo("aa"));
//		System.out.println("firing");
		Random rnd = new Random(System.currentTimeMillis());
		
		List<String[]> hostList = new ArrayList<String[]>();
		hostList.add(new String[]{"127.0.0.1", "20001", "1"});
		PigeonClientMock.getSc().onServiceHostChange("http://service.dianping.com/echoService", hostList );
		
		int i = 0;
		while(i < 100) {
			try {
				System.out.println(s.echo("aa"));
			} catch (Exception e) {
				System.out.println("EEEEEEEEEEEEEEEEEEEEEEE");
			}
			Thread.sleep(2000);
			if(i == 1) {
				System.out.println("+++++++++++++++++++adding 20002++++++++++++++++");
				hostList = new ArrayList<String[]>();
				hostList.add(new String[]{"127.0.0.1", "20001", "1"});
				hostList.add(new String[]{"127.0.0.1", "20002", "1"});
				PigeonClientMock.getSc().onServiceHostChange("http://service.dianping.com/echoService", hostList );
			}
			if(i == 5) {
				System.out.println("+++++++++++++++++++adding 20003++++++++++++++++");
				hostList = new ArrayList<String[]>();
				hostList.add(new String[]{"127.0.0.1", "20001", "1"});
				hostList.add(new String[]{"192.168.32.111", "20003", "1"});
				PigeonClientMock.getSc().onServiceHostChange("http://service.dianping.com/echoService", hostList );
			}
			if(i == 10) {
				System.out.println("--------------------change wt------------");
				PigeonClientMock.getSc().onHostWeightChange("127.0.0.1", 2);
			}
			i++;
		}
		
	}

}
