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
import com.dianping.dpsf.exception.NetException;

/**    
 * <p>    
 * Title: Client.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-3 上午11:39:07   
 */
public interface Client {
	
	public void connect() throws NetException;
	
	public DPSFFuture write(DPSFRequest message,DPSFCallback callback);
	
	public void write(DPSFRequest message);
	
	public void connectionException(Object attachment,ExceptionEvent e);
	
	public List<String> getServiceNames();

	public void addServiceName(String serviceName);

	public void doResponse(DPSFResponse response);
	
	public long getLastMessageTime();
	
	public boolean isConnected();
	
	public boolean isActive();
	public void setActive(boolean active);
	
	public boolean isActiveSetable();
	public void setActiveSetable(boolean activesetable);
	
	public boolean isWritable();
	
	public String getHost();
	public String getAddress();
	
	public int getPort();
	
	public Channel getChannel();
	
	public void close();

}
