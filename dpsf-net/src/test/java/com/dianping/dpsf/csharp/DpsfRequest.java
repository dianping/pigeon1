/**
 * Project: ${dpsf-net.aid}
 * 
 * File Created at 2012-5-27
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
package com.dianping.dpsf.csharp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * TODO Comment of DpsfRequest
 * @author danson.liu
 *
 */
public class DpsfRequest implements Serializable {

	private String name;
	private int age;
	private BigDecimal price;
	private Date birthday;
	private float f1;
	private double f2;
	private InnerRequest[] innerReqs;
	private double additional;
	public double getAdditional() {
		return additional;
	}
	public void setAdditional(double additional) {
		this.additional = additional;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public float getF1() {
		return f1;
	}
	public void setF1(float f1) {
		this.f1 = f1;
	}
	public double getF2() {
		return f2;
	}
	public void setF2(double f2) {
		this.f2 = f2;
	}
	public InnerRequest[] getInnerReqs() {
		return innerReqs;
	}
	public void setInnerReqs(InnerRequest[] innerReqs) {
		this.innerReqs = innerReqs;
	}
	
}
