package com.dianping.test.client.unitest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import com.dianping.dpsf.async.ServiceCallback;
import com.dianping.dpsf.exception.DPSFException;
import com.dianping.test.server.TestSupport;
import com.dianping.test.server.remote.TestParam;
import com.dianping.test.server.remote.TestResult;
import com.dianping.test.server.remote.TestService;

public class TestCallback extends TestSupport{
	
	private String[] config = new String[]{"client-callback.xml"};
	
	public TestService testProxy;
	public TestServiceCallback testServiceCallback;
	
	private Map<String,TestParam> paramMap = new HashMap<String,TestParam>();
	
	private TestResult res1;
	private TestResult res2;
	private List<TestResult> resList;

	@Override
	protected String[] getSpringConfig() {
		return config;
	}
	
	@Before
	public void before(){
		this.init();
		testServiceCallback.setTestCallback(this);
		for(int i=0;i<10;i++){
			paramMap.put(""+i, new TestParam("a"+i,i));
		}
	}
	
	@Test
	public void testRemoteCallback() throws InterruptedException{
		
		Assert.isNull(testProxy.getResult());
		
		Assert.isNull(testProxy.getResult("a",1,new TestParam("b",2,1)));
		
		testProxy.execute();
		
		Assert.isNull(testProxy.getResult(paramMap));
		
		Thread.sleep(3000);
		
		Assert.isTrue(res1.getF1().equals("") && res1.getF2() == 0);
		Assert.isTrue(res2.getF1().equals("ab") && res2.getF2() == 3);
		Assert.isTrue(resList.size() == 10000);
	}
	
	public static class TestServiceCallback implements ServiceCallback{
		
		private TestCallback testCallback;

		@Override
		public void callback(Object result) {

			if(result instanceof TestResult){
				
				TestResult res = (TestResult)result;
				if(res.getF3() == 0){
					testCallback.res1 = res;
				}else if(res.getF3() == 1){
					testCallback.res2 = res;
				}
				
			}else if(result instanceof List){
				testCallback.resList = (List<TestResult>)result;
			}
			System.out.println("*************callback**************");
		}

		@Override
		public void serviceException(Exception e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void frameworkException(DPSFException e) {
			// TODO Auto-generated method stub
			
		}

		public TestCallback getTestCallback() {
			return testCallback;
		}

		public void setTestCallback(TestCallback testCallback) {
			this.testCallback = testCallback;
		}
		
		
	}

}
