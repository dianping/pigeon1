/**
 * 
 */
package com.dianping.dpsf.net.channel.manager;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.jboss.netty.channel.ExceptionEvent;

import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.Invoker;
import com.dianping.dpsf.exception.NetException;
import com.dianping.dpsf.exception.ServiceException;
import com.dianping.dpsf.net.channel.Client;
import com.dianping.dpsf.net.channel.config.HostInfo;
import com.dianping.dpsf.thread.DPSFThreadPool;

/**    
 * <p>    
 * Title: ClientManager.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-9 下午03:01:51   
 */
public interface ClientManager {
	
	public void registeClient(String serviceName,String group,String connect,int weight)throws NetException;
	
	public Client getClient(String serviceName,String group, DPSFRequest request) throws NetException;
	
	public void connectionException(Client client,Object attachment,ExceptionEvent e);
	
	public void setInvoker(Invoker invoker);
	
	public void processResponse(DPSFResponse response,Client client)throws ServiceException;
	
	public DPSFThreadPool getClientResponseThreadPool();
	
	public Executor getBossExecutor();

	public Executor getWorkerExecutor();
	
	/**
	 * 从ZK中获取serviceName对应的服务地址并注册
	 * @param serviceName
	 * @param group
	 */
	public void findAndRegisterClientFor(String serviceName, String group);
	
	/**
	 * 强制设置group的路由列表，和动态服务互斥，给调度中心使用
	 * @param serviceName
	 * @param group
	 * @param connectSet
	 */
	public void setGroupRoute(String serviceName, String group, Set<String> connectSet);
	
	public Map<String, Set<HostInfo>> getServiceHostInfos();

}
