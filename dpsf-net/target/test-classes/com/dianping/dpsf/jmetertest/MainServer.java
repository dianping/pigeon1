package com.dianping.dpsf.jmetertest;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class MainServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ApplicationContext beanFactory = new ClassPathXmlApplicationContext("jmeter/jmeter-server.xml");

	}

}
