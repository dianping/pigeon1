package com.dianping.test.client.unitest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import com.dianping.dpsf.async.ServiceFuture;
import com.dianping.dpsf.async.ServiceFutureFactory;
import com.dianping.dpsf.exception.DPSFException;
import com.dianping.test.server.TestSupport;
import com.dianping.test.server.remote.TestParam;
import com.dianping.test.server.remote.TestResult;
import com.dianping.test.server.remote.TestService;

public class TestFuture extends TestSupport{
	
	private String[] config = new String[]{"client-future.xml"};
	
	public TestService testProxy;
	
	private Map<String,TestParam> paramMap = new HashMap<String,TestParam>();

	@Override
	protected String[] getSpringConfig() {
		return config;
	}
	
	@Before
	public void before(){
		this.init();
		
		for(int i=0;i<10;i++){
			paramMap.put(""+i, new TestParam("a"+i,i));
		}
	}
	
	@Test
	public void testRemoteFuture() throws DPSFException, InterruptedException{
		
		testProxy.getResult();
		ServiceFuture future1 = ServiceFutureFactory.getFuture();
		
		
		testProxy.getResult("a",1,new TestParam("b",2));
		ServiceFuture future2 = ServiceFutureFactory.getFuture();
		
		
		testProxy.execute();
		ServiceFuture future3 = ServiceFutureFactory.getFuture();
		
		testProxy.getResult(paramMap);
		ServiceFuture future4 = ServiceFutureFactory.getFuture();
		
		
		TestResult res1 = (TestResult)future1._get();
		TestResult res2 = (TestResult)future2._get();
		TestResult res3 = (TestResult)future3._get();
		List<TestResult> resList = (List<TestResult>)future4._get();
		Assert.isTrue(res1.getF1().equals("") && res1.getF2() == 0);
		Assert.isTrue(res2.getF1().equals("ab") && res2.getF2() == 3);
		Assert.isNull(res3);
		Assert.isTrue(resList.size() == 10000);
	}

}
