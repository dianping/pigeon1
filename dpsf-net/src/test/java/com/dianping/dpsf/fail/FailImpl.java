package com.dianping.dpsf.fail;

public class FailImpl implements Fail{

	@Override
	public TestResult testClientSerializeFail(TestParameterNoSerializable param) {
		return new TestResult(param.getParameter()+1);
	}

	@Override
	public TestResultNoSerializable testServerSerializeFail(TestParameter param) {
		return new TestResultNoSerializable(param.getParameter()+1);
	}

	@Override
	public TestResult testSuccess(TestParameter param) {
		return new TestResult(param.getParameter()+1);
	}

	@Override
	public TestResult testException() throws TestException{
		throw new TestException("test exception");
	}

}
