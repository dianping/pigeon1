package com.dianping.test.client.liontest;

import org.junit.Before;
import org.junit.Test;

import com.dianping.test.server.TestSupport;
import com.dianping.test.server.remote.TestLionService;

public class TestLion extends TestSupport{
	
	private String[] config = new String[]{"client-lion.xml"};
	
	public TestLionService testProxy;
	

	@Override
	protected String[] getSpringConfig() {
		return config;
	}
	
	@Before
	public void before(){
		this.init();
	}
	
	@Test
	public void testLionManager() throws InterruptedException{
		
		while(true){
			String res = testProxy.execute();
			System.out.println("<<<<<<<<<<<<<<<<<<<<"+res+">>>>>>>>>>>>>>>>>>>");
			
			Thread.sleep(1000);
		}
		
	}

}
