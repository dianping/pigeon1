/**
 * 
 */
package com.dianping.dpsf.net.channel;

import java.util.List;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ExceptionEvent;

import com.dianping.dpsf.component.DPSFCallback;
import com.dianping.dpsf.component.DPSFFuture;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.DPSFResponse;

/**
 * <p>
 * Title: Client.java
 * </p>
 * <p>
 * Description: 描述
 * </p>
 * 
 * @author saber miao
 * @version 1.0
 * @created 2010-8-3 上午11:39:07
 */
public interface Client {

	void connect() ;

	DPSFFuture write(DPSFRequest message, DPSFCallback callback);

	void write(DPSFRequest message);

	void connectionException(Object attachment, ExceptionEvent e);

	List<String> getServiceNames();

	void addServiceName(String serviceName);

	void doResponse(DPSFResponse response);

	long getLastMessageTime();

	boolean isConnected();

	boolean isActive();

	void setActive(boolean active);

	boolean isActiveSetable();

	void setActiveSetable(boolean activesetable);

	boolean isWritable();

	String getHost();

	String getAddress();

	int getPort();

	Channel getChannel();

	void close();

}
