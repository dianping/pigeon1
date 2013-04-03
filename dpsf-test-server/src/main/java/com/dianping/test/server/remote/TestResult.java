package com.dianping.test.server.remote;

import java.io.Serializable;

public class TestResult implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3551440939380063238L;
	
	

	public TestResult(String f1, int f2) {
		super();
		this.f1 = f1;
		this.f2 = f2;
	}
	
	public String f1;
	
	public int f2;
	
	private int f3 = 0;

	public String getF1() {
		return f1;
	}

	public void setF1(String f1) {
		this.f1 = f1;
	}

	public int getF2() {
		return f2;
	}

	public void setF2(int f2) {
		this.f2 = f2;
	}
	
	public int getF3(){
		return this.f3;
	}
	
	public void setF3(int f3){
		this.f3 = f3;
	}

}
