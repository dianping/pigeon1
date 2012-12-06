/**
 * 
 */
package com.dianping.dpsf.net.channel.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.net.channel.Client;
import com.dianping.dpsf.net.channel.manager.LionNotifier;
import com.dianping.dpsf.net.channel.manager.ServiceProviderChangeEvent;
import com.dianping.dpsf.net.channel.manager.ServiceProviderChangeListener;

/**    
 * <p>    
 * Title: ClusterClient.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-9-26 下午06:23:48   
 */
public class ClusterConfigure implements Configure{
	
	private static Logger logger = DPSFLog.getLogger();
	
	public static final String PLACEHOLDER = ":";

	private static Configure configure = new ClusterConfigure();
	
	private Map<String,ConnectMetaData> connectMetaDataMap = new ConcurrentHashMap<String,ConnectMetaData>();
	
	private List<ClusterListener> listeners = new ArrayList<ClusterListener>();
	
	private ClusterConfigure() {
		LionNotifier.addListener(new ServiceProviderChangeListener() {
			
			@Override
			public void hostWeightChanged(ServiceProviderChangeEvent event) {
			}
			
			@Override
			public void providerAdded(ServiceProviderChangeEvent event) {
			}
			
			@Override
			public void providerRemoved(ServiceProviderChangeEvent event) {
				//addConnect的逆操作
				String connect = event.getHost() + ":" + event.getPort();
				logger.info("remove " + connect + " from " + event.getServiceName());
				ConnectMetaData cmd = connectMetaDataMap.get(connect);
				if(cmd != null) {
					cmd.getServiceNames().remove(event.getServiceName());
					if(cmd.getServiceNames().size() == 0) {
						connectMetaDataMap.remove(connect);
					}
				}
				for (ClusterListener listener : listeners) {
					listener.doNotUse(event.getServiceName(), event.getHost(), event.getPort());
				}
				
			}
		});
	}

	public static Configure getInstance(){
		return configure;
	}
	
//	public void addConnect(String connect){
//		ConnectMetaData cmd = this.connectMetaDataMap.get(connect);
//		if(cmd != null){
//			for(ClusterListener listener : listeners){
//				listener.addConnect(cmd);
//			}
//		}
//	}
	
	public synchronized void addConnect(String connect,Client client){
		ConnectMetaData cmd = this.connectMetaDataMap.get(connect);
		if(cmd != null){
			for(ClusterListener listener : listeners){
				listener.addConnect(cmd,client);
			}
		}
	}
	
	public synchronized void addConnect(ConnectMetaData cmd){
		ConnectMetaData metaData = this.connectMetaDataMap.get(cmd.getConnect());
		if(metaData == null){
			this.connectMetaDataMap.put(cmd.getConnect(), cmd);
		}else{
			metaData.addServiceNames(cmd.getServiceNames());
		}
		for(ClusterListener listener : listeners){
			listener.addConnect(cmd);
		}
	}
	public synchronized void removeConnect(String connect){
		ConnectMetaData cmd = this.connectMetaDataMap.get(connect);
		if(cmd != null){
			for(ClusterListener listener : listeners){
				listener.removeConnect(cmd);
			}
		}
	}
	
	public void addListener(ClusterListener listener){
		this.listeners.add(listener);
	}
}
