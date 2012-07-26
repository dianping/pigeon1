/**
 * 
 */
package com.dianping.dpsf.net.channel.config;

import com.dianping.dpsf.net.channel.Client;

/**
 * <p>
 * Title: ClusterListener.java
 * </p>
 * <p>
 * Description: 描述
 * </p>
 * 
 * @author saber miao
 * @version 1.0
 * @created 2010-9-26 下午09:47:11
 */
public interface ClusterListener {

	void addConnect(ConnectMetaData cmd);

	void addConnect(ConnectMetaData cmd, Client client);

	void removeConnect(ConnectMetaData cmd);

	void doNotUse(String serviceName, String host, int port);

}
