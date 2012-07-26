/**
 * 
 */
package com.dianping.dpsf.tserver;

import java.util.ArrayList;
import java.util.List;

public class ExampleServiceImpl implements ExampleService {

	private String str;

	public List<ReturnVal> service(int id, String name, ParameterVal parameter) throws ExampleException {
		List<ReturnVal> rs = new ArrayList<ReturnVal>();
		for (int i = 0; i < id; i++) {
			rs.add(new ReturnVal(String.format("id:%s,name:%s,parameter:%s", id, name, parameter)));
		}
		return rs;
	}

	public ReturnVal service(String name, ParameterVal parameter) {
		return new ReturnVal(String.format("name:%s,parameter:%s", name, parameter));
	}

	public void longTimeService() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

}
