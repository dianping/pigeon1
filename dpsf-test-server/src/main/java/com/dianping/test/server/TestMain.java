package com.dianping.test.server;

public class TestMain extends TestSupport{
	
	private String[] config = new String[]{"server.xml"};

	@Override
	protected String[] getSpringConfig() {
		return config;
	}
	
	public static void main(String[] args) throws InterruptedException{
		TestMain testMain = new TestMain();
		testMain.init();
		
		Thread.sleep(50000000);
	}
	

}
