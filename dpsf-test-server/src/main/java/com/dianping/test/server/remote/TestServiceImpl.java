package com.dianping.test.server.remote;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TestServiceImpl implements TestService{
	
	@Override
	public TestResult getResult() {
		return new TestResult("",0);
	}

	@Override
	public TestResult getResult(String param1, int param2, TestParam param3) {
		TestResult res = new TestResult(param1+param3.getF1(),param2+param3.getF2());
		res.setF3(param3.getF3());
		return res;
	}

	@Override
	public void execute() {
		
	}

	@Override
	public List<TestResult> getResult(Map<String, TestParam> paramMap) {
		
		List<TestResult> res = new ArrayList<TestResult>();

		for(int i=0;i<1000;i++){
			for(Entry<String,TestParam> paramEntry : paramMap.entrySet()){
				res.add(new TestResult(paramEntry.getKey(),paramEntry.getValue().getF2()));
			}
		}
		return res;
	}

}
