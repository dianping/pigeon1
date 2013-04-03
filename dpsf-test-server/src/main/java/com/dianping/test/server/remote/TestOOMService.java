package com.dianping.test.server.remote;

import java.util.List;
import java.util.Map;

public interface TestOOMService {
	
	public TestResult getResult();
	
	public TestResult getResult(String param1,int param2,TestParam param3);
	
	public List<TestResult> getResult(Map<String,TestParam> paramMap);
	
	public void execute();

}
