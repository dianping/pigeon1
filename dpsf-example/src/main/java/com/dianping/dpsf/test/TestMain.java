package com.dianping.dpsf.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestMain {


	public static void main(String[] args) throws Throwable {
		ApplicationContext beanFactory = new ClassPathXmlApplicationContext(new String[] { "test-server.xml", "test-client.xml" });
		TestIFace bean = (TestIFace) beanFactory.getBean("testClient");

		int value = bean.test(1);
		System.out.println(">>>>>>>>>>>>>>>>>>>>>" + value);
	}

}
