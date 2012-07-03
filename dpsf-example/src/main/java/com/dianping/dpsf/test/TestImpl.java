package com.dianping.dpsf.test;

public class TestImpl implements TestIFace{

	@Override
	public int test(int arg) throws TestException {
		if(arg > 0){
			return arg;
		}else{
			throw new TestException("Test");
		}
	}

}
