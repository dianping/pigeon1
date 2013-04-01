package com.dianping.test.server.remote;


public interface TestFailOverService {
	
	public TestResult getRetryResult1(TestParam param);
	
	public TestResult getRetryResult2(TestParam param);
}
