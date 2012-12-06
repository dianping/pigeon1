/**
 * Project: dpsf-net
 * 
 * File Created at 2011-8-15
 * $Id$
 * 
 * Copyright 2011 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dianping.dpsf.net.channel.manager;

/**
 * 服务动态调整信息listener
 * @author marsqing
 *
 */
public interface ServiceProviderChangeListener {

	/**
	 * 新增provider
	 * @param event
	 */
	public void providerAdded(ServiceProviderChangeEvent event);
	
	/**
	 * 删除provider
	 * @param event
	 */
	public void providerRemoved(ServiceProviderChangeEvent event);
	
	/**
	 * host权重发生变化
	 * @param event
	 */
	public void hostWeightChanged(ServiceProviderChangeEvent event);
	
}
