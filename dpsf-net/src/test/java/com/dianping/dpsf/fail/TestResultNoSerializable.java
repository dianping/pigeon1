package com.dianping.dpsf.fail;


public class TestResultNoSerializable{

	private int result = 0;
	
	public TestResultNoSerializable(int result){
		this.result = result;
	}
	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}
}
