package com.dianping.test.client.unitest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import com.dianping.test.server.TestSupport;
import com.dianping.test.server.remote.TestParam;
import com.dianping.test.server.remote.TestResult;
import com.dianping.test.server.remote.TestService;

public class TestSync extends TestSupport{
	
	private String[] config = new String[]{"client-sync.xml"};
	
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
	public void testRemoteSync(){
		
		TestResult res1 = testProxy.getResult();
		Assert.isTrue(res1.getF1().equals("") && res1.getF2() == 0);
		
		TestResult res2 = testProxy.getResult("a",1,new TestParam("b",2));
		Assert.isTrue(res2.getF1().equals("ab") && res2.getF2() == 3);
		
		testProxy.execute();
		
		List<TestResult> resList = testProxy.getResult(paramMap);
		Assert.isTrue(resList.size() == 10000);
	}

}
