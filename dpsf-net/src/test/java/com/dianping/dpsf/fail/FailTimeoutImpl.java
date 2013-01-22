package com.dianping.dpsf.fail;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.ContextUtil;

public class FailTimeoutImpl implements FailTimeout{
	
	private FailTimeout failTimeout;

	private long createTime;
	private int timeout;
	private boolean firstFlag = false;
	public int testCallNormal1(int param){
		this.createTime = Long.parseLong(String.valueOf(ContextUtil.getLocalContext(Constants.REQUEST_CREATE_TIME)));
		this.timeout = Integer.parseInt(String.valueOf(ContextUtil.getLocalContext(Constants.REQUEST_TIMEOUT)));
		return this.failTimeout.testCallNormal2(param)+1;
	}

	@Override
	public int testCallNormal2(int param) {
		this.createTime = Long.parseLong(String.valueOf(ContextUtil.getLocalContext(Constants.REQUEST_CREATE_TIME)));
		this.timeout = Integer.parseInt(String.valueOf(ContextUtil.getLocalContext(Constants.REQUEST_TIMEOUT)));
		return this.failTimeout.testCallNormal3(param)+1;
	}

	@Override
	public int testCallNormal3(int param) {
		this.createTime = Long.parseLong(String.valueOf(ContextUtil.getLocalContext(Constants.REQUEST_CREATE_TIME)));
		this.timeout = Integer.parseInt(String.valueOf(ContextUtil.getLocalContext(Constants.REQUEST_TIMEOUT)));
//		try {
//			Thread.currentThread().sleep(2000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return param+1;
	}

	@Override
	public int testCallTimeout1(int param) {
		this.createTime = Long.parseLong(String.valueOf(ContextUtil.getLocalContext(Constants.REQUEST_CREATE_TIME)));
		this.timeout = Integer.parseInt(String.valueOf(ContextUtil.getLocalContext(Constants.REQUEST_TIMEOUT)));
		return this.failTimeout.testCallTimeout2(param)+1;
	}

	@Override
	public int testCallTimeout2(int param) {
		this.createTime = Long.parseLong(String.valueOf(ContextUtil.getLocalContext(Constants.REQUEST_CREATE_TIME)));
		this.timeout = Integer.parseInt(String.valueOf(ContextUtil.getLocalContext(Constants.REQUEST_TIMEOUT)));
		return this.failTimeout.testCallTimeout3(param)+1;
	}

	@Override
	public int testCallTimeout3(int param) {
		this.createTime = Long.parseLong(String.valueOf(ContextUtil.getLocalContext(Constants.REQUEST_CREATE_TIME)));
		this.timeout = Integer.parseInt(String.valueOf(ContextUtil.getLocalContext(Constants.REQUEST_TIMEOUT)));
		try {
			Thread.currentThread().sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return param+1;
	}

	public int testCallNormal4(int param){
		int res = this.failTimeout.testCallNormal5(param)+1;
		System.out.println(">>>>>>>>>param:"+param+" res:"+res);
		this.createTime = Long.parseLong(String.valueOf(ContextUtil.getLocalContext(Constants.REQUEST_CREATE_TIME)));
		this.timeout = Integer.parseInt(String.valueOf(ContextUtil.getLocalContext(Constants.REQUEST_TIMEOUT)));
		this.firstFlag = Boolean.parseBoolean(String.valueOf(ContextUtil.getLocalContext(Constants.REQUEST_FIRST_FLAG)));
		return res;
	}

	@Override
	public int testCallNormal5(int param) {

		int res = this.failTimeout.testCallNormal6(param)+1 + this.failTimeout.testCallNormal6(param)+1;
		System.out.println(">>>>>>>>>param:"+param+" res:"+res);
		this.createTime = Long.parseLong(String.valueOf(ContextUtil.getLocalContext(Constants.REQUEST_CREATE_TIME)));
		this.timeout = Integer.parseInt(String.valueOf(ContextUtil.getLocalContext(Constants.REQUEST_TIMEOUT)));
		this.firstFlag = Boolean.parseBoolean(String.valueOf(ContextUtil.getLocalContext(Constants.REQUEST_FIRST_FLAG)));

		return res;
	}

	@Override
	public int testCallNormal6(int param) {
		this.createTime = Long.parseLong(String.valueOf(ContextUtil.getLocalContext(Constants.REQUEST_CREATE_TIME)));
		this.timeout = Integer.parseInt(String.valueOf(ContextUtil.getLocalContext(Constants.REQUEST_TIMEOUT)));
		this.firstFlag = Boolean.parseBoolean(String.valueOf(ContextUtil.getLocalContext(Constants.REQUEST_FIRST_FLAG)));
//		try {
//			Thread.currentThread().sleep(2000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		int res = param+1;
		System.out.println(">>>>>>>>>param:"+param+" res:"+res);
		return res;
	}

	@Override
	public int testCallTimeout4(int param) {
		this.createTime = Long.parseLong(String.valueOf(ContextUtil.getLocalContext(Constants.REQUEST_CREATE_TIME)));
		this.timeout = Integer.parseInt(String.valueOf(ContextUtil.getLocalContext(Constants.REQUEST_TIMEOUT)));
		return this.failTimeout.testCallTimeout5(param)+1;
	}

	@Override
	public int testCallTimeout5(int param) {
		this.createTime = Long.parseLong(String.valueOf(ContextUtil.getLocalContext(Constants.REQUEST_CREATE_TIME)));
		this.timeout = Integer.parseInt(String.valueOf(ContextUtil.getLocalContext(Constants.REQUEST_TIMEOUT)));
		return this.failTimeout.testCallTimeout6(param)+1 + this.failTimeout.testCallTimeout6(param)+1 + this.failTimeout.testCallTimeout6(param)+1;
	}

	int k = 0;
	@Override
	public int testCallTimeout6(int param) {

		this.createTime = Long.parseLong(String.valueOf(ContextUtil.getLocalContext(Constants.REQUEST_CREATE_TIME)));
		this.timeout = Integer.parseInt(String.valueOf(ContextUtil.getLocalContext(Constants.REQUEST_TIMEOUT)));
		System.out.println("<><><><><><><><><><>"+this.timeout);
		try {
			Thread.currentThread().sleep(1600);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return param+1;
	}

	public FailTimeout getFailTimeout() {
		return failTimeout;
	}

	public void setFailTimeout(FailTimeout failTimeout) {
		this.failTimeout = failTimeout;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public boolean isFirstFlag() {
		return firstFlag;
	}

}
