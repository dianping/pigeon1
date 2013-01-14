package com.dianping.dpsf.other.echo;

import java.util.concurrent.CountDownLatch;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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

public class EchoServer3 {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		Thread.currentThread().setName("=============EchoSerer3============");
		ApplicationContext ctx = new ClassPathXmlApplicationContext("echo-server3.xml");
		if(args.length == 0) {	//stand alone start
			CountDownLatch latch = new CountDownLatch(1);
			latch.await();
		}
	}

}
