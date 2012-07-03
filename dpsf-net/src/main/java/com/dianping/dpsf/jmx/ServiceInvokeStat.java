/**
 * Project: ${dpsf-net.aid}
 * 
 * File Created at 2012-3-25
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
package com.dianping.dpsf.jmx;

import java.io.Serializable;

/**
 * ServcieInvokeStat
 * @author danson.liu
 *
 */
public class ServiceInvokeStat implements Serializable {

	private static final long serialVersionUID = -2675494032921824026L;
	
	private String connect;
	
	private boolean connected;
	
	private boolean active;
	
	private int weight;
	
	private float capacity;
	
	private long requestSend;
	
	private long onewayRequestSend;
	
	private int requestSendLastSec;		//上一秒发送请求数
	
	public ServiceInvokeStat() {
	}

	public ServiceInvokeStat(String connect) {
		this.connect = connect;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public float getCapacity() {
		return capacity;
	}

	public void setCapacity(float capacity) {
		this.capacity = capacity;
	}

	public long getRequestSend() {
		return requestSend;
	}

	public void setRequestSend(long requestSend) {
		this.requestSend = requestSend;
	}

	public long getOnewayRequestSend() {
		return onewayRequestSend;
	}

	public void setOnewayRequestSend(long onewayRequestSend) {
		this.onewayRequestSend = onewayRequestSend;
	}

	public int getRequestSendLastSec() {
		return requestSendLastSec;
	}

	public void setRequestSendLastSec(int requestSendLastSec) {
		this.requestSendLastSec = requestSendLastSec;
	}

	public String getConnect() {
		return connect;
	}

	public void setConnect(String connect) {
		this.connect = connect;
	}

}
