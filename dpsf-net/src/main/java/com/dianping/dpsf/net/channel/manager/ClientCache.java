/**
 * 
 */
package com.dianping.dpsf.net.channel.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.exception.NetException;
import com.dianping.dpsf.net.channel.Client;
import com.dianping.dpsf.net.channel.config.ClusterConfigure;
import com.dianping.dpsf.net.channel.config.ClusterListener;
import com.dianping.dpsf.net.channel.config.Configure;
import com.dianping.dpsf.net.channel.config.ConnectMetaData;
import com.dianping.dpsf.net.channel.netty.client.NettyClient;
import com.dianping.dpsf.thread.DefaultThreadFactory;

/**    
 * <p>    
 * Title: ClientCache.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-9 下午04:56:45   
 */
public class ClientCache implements ClusterListener{
	
	private static Logger logger = DPSFLog.getLogger();
	
	private ClientManager clientManager;
	
	private Map<String,List<Client>> serviceClients = new ConcurrentHashMap<String,List<Client>>();
		 
	private Map<String,Client> allClients = new ConcurrentHashMap<String,Client>();
	
	private HeartBeatTask heartTask;
	
	private ReconnectTask reconnectTask;

	private ScheduledThreadPoolExecutor closeExecutor;

    private Configure clusterConfigure;
	
	public ClientCache(ClientManager clientManager, HeartBeatTask heartTask, ReconnectTask reconnectTask, Configure clusterConfigure){
		this.clientManager = clientManager;
		this.heartTask = heartTask;
		this.reconnectTask = reconnectTask;
        this.clusterConfigure = clusterConfigure;
		this.heartTask.setWorkingClients(this.serviceClients);
		closeExecutor = new ScheduledThreadPoolExecutor(5,new DefaultThreadFactory("ClientCache-CloseExecutor"));
	}
	
	public List<Client> getClientList(String serviceName) throws NetException{
		List<Client> clientList = this.serviceClients.get(serviceName);
		
		if(clientList == null || clientList.size() == 0){
			throw new NetException("no connection for serviceName:"
					+serviceName);
		}
		return clientList;
	}
	
	public synchronized void addConnect(ConnectMetaData cmd) {
		addConnect(cmd,this.allClients.get(cmd.getConnect()));
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.channel.config.ClusterListener#addConnect(java.lang.String, java.lang.String)
	 */
	public synchronized void addConnect(ConnectMetaData cmd,Client client) {
		String connect = cmd.getConnect();
		
		if(clientExisted(cmd)) {
			if(client != null) {
				for (List<Client> clientList : serviceClients.values()) {
					int idx = clientList.indexOf(client);
					if(idx >= 0 && clientList.get(idx) != client) {	//equals but no ==
						closeClientInFuture(client);
					}
				}
			} else {
				return;
			}
		}
		
		if(client == null){
			client = new NettyClient(cmd.getHost(),cmd.getPort(),this.clientManager);
		}
		
		if(!this.allClients.containsKey(connect)){
			this.allClients.put(connect, client);
		}
		
		try {
			if(!client.isConnected()){
				client.connect();
			}
			if(client.isConnected()){
				for(Entry<String,Integer> sw : cmd.getServiceNames().entrySet()){
					String serviceName = sw.getKey();
					List<Client> clientList = this.serviceClients.get(serviceName);
					if(clientList == null){
						clientList = new ArrayList<Client>();
						this.serviceClients.put(serviceName, clientList);
					}
					if(!clientList.contains(client))
						clientList.add(client);
				}
			} else {
			    clusterConfigure.removeConnect(connect);
			}
			
		} catch (NetException e) {
			
			logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * 检查是否已经有cmd对应的Client，避免重复添加Client
	 * @param cmd
	 * @return
	 */
	private boolean clientExisted(ConnectMetaData cmd) {
		boolean existed = true;
		for (String serviceName : cmd.getServiceNames().keySet()) {
			List<Client> clientList = serviceClients.get(serviceName);
			if(clientList == null) {
				existed = false;
				break;
			}
			boolean findClient = false;
			for (Client client : clientList) {
				if(client.getAddress().equals(cmd.getConnect())) {
					findClient = true;
				}
			}
			if(!findClient) {
				existed = false;
				break;
			}
		}
		return existed;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.channel.config.ClusterListener#removeConnect(java.lang.String)
	 */
	public synchronized void removeConnect(ConnectMetaData cmd) {
		String connect = cmd.getConnect();
		Client client = this.allClients.remove(connect);
		if(client != null){
			for(String serviceName : this.serviceClients.keySet()){
				List<Client> clientList = this.serviceClients.get(serviceName);
				if(clientList != null && clientList.contains(client)){
					clientList.remove(client);
				}
			}
		}
		
	}

	/**
	 * @return the allClients
	 */
	public Map<String, Client> getAllClients() {
		return allClients;
	}

	@Override
	public synchronized void doNotUse(String serviceName, String host, int port) {
		List<Client> cs = serviceClients.get(serviceName);
		List<Client> newCS = new ArrayList<Client>(cs);
		Client clientFound = null;
		for (Client client : cs) {
			if(client.getHost().equals(host) && client.getPort() == port) {
				newCS.remove(client);
				clientFound = client;
			}
		}
		serviceClients.put(serviceName, newCS);
		
		//一个client可能对应多个serviceName，仅当client不被任何serviceName使用时才关闭
		if(clientFound != null) {
			if(!isClientInUse(clientFound)) {
				removeClientFromReconnectTask(clientFound);
				allClients.remove(clientFound.getAddress());
				closeClientInFuture(clientFound);
			}
		}
	}
	
	//move to HeartTask?
	private void removeClientFromReconnectTask(Client clientToRemove) {
		Map<String, Client> closedClients = reconnectTask.getClosedClients();
		Set<String> keySet = closedClients.keySet();
		Iterator<String> iterator = keySet.iterator();
		while (iterator.hasNext()) {
			String connect = iterator.next();
			if (closedClients.get(connect).equals(clientToRemove)) {
				iterator.remove();
			}
		}
	}
	
	private boolean isClientInUse(Client clientToFind) {
		for (List<Client> clientList : serviceClients.values()) {
			if(clientList.contains(clientToFind)) {
				return true;
			}
		}
		return false;
	}

	private void closeClientInFuture(final Client client) {
		Runnable command = new Runnable() {

			@Override
			public void run() {
				client.close();
			}
			
		};
		try {
			String waitTimeStr = System.getProperty("com.dianping.pigeon.client.closewaittime");
			int waitTime = 3000;
			if(waitTimeStr != null) {
				try{
					waitTime = Integer.parseInt(waitTimeStr);
				} catch (Exception e) {
					logger.error("error parsing com.dianping.pigeon.client.closewaittime" , e);
				}
			}
			if(waitTime < 0) {
				waitTime = 3000;
			}
			closeExecutor.schedule(command, waitTime, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			logger.error("error schedule task to close client", e);
		}
	}

}
