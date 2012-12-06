/**
 * 
 */
package com.dianping.dpsf.net.channel.config;

import com.dianping.dpsf.net.channel.Client;

/**
 * <p>
 * Title: config.java
 * </p>
 * <p>
 * Description: 描述
 * </p>
 * 
 * @author saber miao
 * @version 1.0
 * @created 2010-9-26 下午06:08:14
 */
public interface Configure {

	void addConnect(String connect, Client client);

	// void addConnect(String connect);

	void addConnect(ConnectMetaData cmd);

	void removeConnect(String connect);

	void addListener(ClusterListener listener);

}
