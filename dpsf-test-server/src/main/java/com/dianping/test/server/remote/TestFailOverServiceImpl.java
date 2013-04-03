package com.dianping.test.server.remote;

import java.util.concurrent.atomic.AtomicInteger;


public class TestFailOverServiceImpl implements TestFailOverService{
	
	private int sleepTime = 1000;
	
	private static AtomicInteger callTimes1 = new AtomicInteger(0);
	
	public TestResult getRetryResult1(TestParam param){
		int callTimes = 0;
		if((callTimes = callTimes1.incrementAndGet())%2 == 1){
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("<<<<<<<<<<<<<<<<<<getRetryResult1 "+callTimes+" call sleepTime:"+sleepTime+">>>>>>>>>>>>>>>>>>>>>>>");
		
		return new TestResult("a"+param.getF1(),1+param.getF2());
	}
	
	public TestResult getRetryResult2(TestParam param){
			throw new RuntimeException("Retry Exception");
	}

	public int getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}
	
}
