/**
 * Project: ${dpsf-net.aid}
 * 
 * File Created at 2012-5-15
 * $Id$
 * 
 * Copyright 2010 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dianping.dpsf.support;

/**
 * @author danson.liu
 *
 */
public class DemoServiceImpl implements DemoService {
	
	private int sleepMsecond;
    private boolean sayEvenError;

    public DemoServiceImpl(int sleepMsecond, boolean sayEvenError) {
		this.sleepMsecond = sleepMsecond;
        this.sayEvenError = sayEvenError;
    }

	@Override
	public String echo(String message) {
		return "hello " + message;
	}
	
	public void echoWithError(boolean runtimeError) throws BusinessException {
		if (runtimeError) {
			throw new RuntimeException("runtime error happen");
		} else {
			throw new BusinessException("error happen");
		}
	}

    public void say(int num) throws BusinessException {
        if ((sayEvenError && num % 2 == 0) || (!sayEvenError && num % 2 == 1)) {
            throw new BusinessException("error happen when say num[" + num + "].");
        }
    }
	
	public void sleep() {
		try {
			Thread.sleep(sleepMsecond);
		} catch (InterruptedException e) {}
	}
	
	public void sleep(long second) {
		try {
			Thread.sleep(second * 1000);
		} catch (InterruptedException e) {}
	}

}
