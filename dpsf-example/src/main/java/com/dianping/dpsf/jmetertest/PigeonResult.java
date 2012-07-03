package com.dianping.dpsf.jmetertest;

import org.apache.jmeter.samplers.SampleResult;

public class PigeonResult extends SampleResult{
	
	public void sampleStart(){
//		if (getStartTime() == 0) {
            setStartTime(System.nanoTime());
//        }
	} 
	
	public void sampleEnd(){
//		 if (getEndTime() == 0) {
	            setEndTime(System.nanoTime());
//	     }
	}

}
