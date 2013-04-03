package com.dianping.test.client.unitest;

import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import com.dianping.test.server.TestSupport;
import com.dianping.test.server.remote.TestFailOverService;
import com.dianping.test.server.remote.TestParam;
import com.dianping.test.server.remote.TestResult;

public class TestFailOver extends TestSupport{
	
	private String[] config = new String[]{"client-failover.xml"};
	
	public TestFailOverService testFailOverProxy;
	

	@Override
	protected String[] getSpringConfig() {
		return config;
	}
	
	@Before
	public void before(){
		this.init();
	}
	
	@Test
	public void testRemoteFailOver(){
		
		TestResult res1 = testFailOverProxy.getRetryResult1(new TestParam("b",1));
		Assert.isTrue(res1.getF1().equals("ab") && res1.getF2() == 2);
		
//		TestResult res2 = testFailOverProxy.getRetryResult2(new TestParam("b",1));
//		Assert.isTrue(res2.getF1().equals("ab") && res2.getF2() == 2);
		
	}

}
