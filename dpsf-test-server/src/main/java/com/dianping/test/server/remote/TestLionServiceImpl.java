package com.dianping.test.server.remote;

public class TestLionServiceImpl implements TestLionService{
	
	private String instanceName = "default";
	
	@Override
	public String execute() {
		System.out.println(this.instanceName + " is executing........................");
		
		return this.instanceName;
	}
	
	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}
}
