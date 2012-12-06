package com.dianping.dpsf.fail;

import java.io.Serializable;

public class TestResult implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4489979731953478796L;

	private int result = 0;
	
	public TestResult(int result){
		this.result = result;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

}
