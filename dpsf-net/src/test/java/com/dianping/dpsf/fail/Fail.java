package com.dianping.dpsf.fail;

public interface Fail {
	
	public TestResult testSuccess(TestParameter param);
	
	public TestResult testClientSerializeFail(TestParameterNoSerializable param);
	
	public TestResultNoSerializable testServerSerializeFail(TestParameter param);
	
	public TestResult testException()throws TestException;

}
