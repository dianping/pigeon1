/**
 * 
 */
package com.dianping.dpsf.net.channel.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.exception.DPSFRuntimeException;
import com.dianping.dpsf.exception.NetException;
import com.dianping.dpsf.exception.NoConnectionException;
import com.dianping.dpsf.net.channel.Client;
import com.dianping.dpsf.net.channel.cluster.LoadBalance;
import com.dianping.dpsf.net.channel.cluster.WeightAccessor;
import com.dianping.dpsf.net.channel.config.ClusterConfigure;

/**    
 * <p>    
 * Title: RouteManager.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-9 下午05:37:59   
 */
public class RouteManager implements WeightAccessor {
	
	private static Logger logger = DPSFLog.getLogger();
	
	private Map<String,Map<String,Integer>> weights_ = new ConcurrentHashMap<String,Map<String,Integer>>();
	private Map<String,Map<String,Integer>> weights = new ConcurrentHashMap<String,Map<String,Integer>>();
	
	private Map<String,Set<String>> groupMap = new ConcurrentHashMap<String,Set<String>>();
	
	private Map<String,List<Integer>> weightCache = new ConcurrentHashMap<String,List<Integer>>();
	
	public RouteManager() {
		LionNotifier.addListener(new ServiceProviderChangeListener() {
			
			@Override
			public void hostWeightChanged(ServiceProviderChangeEvent event) {
				setWeight(event.getConnect(), event.getWeight());
			}
			
			@Override
			public void providerAdded(ServiceProviderChangeEvent event) {
			}
			
			@Override
			public void providerRemoved(ServiceProviderChangeEvent event) {
			}
		});
	}
	
	public boolean enableGroupRouteToHost(String group, String connect) {
		if(group == null || connect == null) {
			return false;
		}
		Set<String> connectSet = groupMap.get(group);
		if(connectSet != null) {
			connectSet.add(connect);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean disableGroupRouteToHost(String group, String connect) {
		if(group == null || connect == null) {
			return false;
		}
		Set<String> connectSet = groupMap.get(group);
		if(connectSet != null) {
			connectSet.remove(connect);
			return true;
		} else {
			return false;
		}
	}
	
	public void setGroupRoute(String group, Set<String> connectSet) {
		groupMap.put(group, connectSet);
	}

	public Client route(List<Client> clientList,String serviceName,String group, DPSFRequest request)throws NetException{
		if (logger.isDebugEnabled()) {
			logger.debug("Routing from: ");
			for (Client client : clientList) {
				logger.debug("\t" + client.getAddress());
			}
		}
		Boolean isWriteBufferLimit = (Boolean) request.getAttachment(Constants.REQ_ATTACH_WRITE_BUFF_LIMIT);
		isWriteBufferLimit = (isWriteBufferLimit != null ? isWriteBufferLimit : false) && request.getCallType() == Constants.CALLTYPE_NOREPLY;
		List<Client> availableClients = filterWithGroupAndWeight(clientList, serviceName, group, isWriteBufferLimit);
		Client selectedClient = select(availableClients, serviceName, group, request);
		checkClientNotNull(selectedClient, serviceName, group);
		
		while (!selectedClient.isConnected()) {
			logger.error("Client is disconnected " + selectedClient.getAddress());
			ClusterConfigure.getInstance().removeConnect(selectedClient.getAddress());
			availableClients.remove(selectedClient);
			if (availableClients.isEmpty()) {
				break;
			}
			selectedClient = select(availableClients, serviceName, group, request);
			checkClientNotNull(selectedClient, serviceName, group);
		}
		
		if (!selectedClient.isConnected()) {
			throw new NoConnectionException("No available server exists for service[" + serviceName + "] and group[" + group + "].");
		}
		return selectedClient;
	}
	
	private void checkClientNotNull(Client client, String serviceName, String group) {
		if (client == null) {
			throw new NoConnectionException("No available server exists for service[" + serviceName + "] and group[" + group + "].");
		}
	}
	
	private Client select(List<Client> availableClients, String serviceName, String group, DPSFRequest request) {
		LoadBalance loadBalance = LoadBalanceManager.getLoadBalance(serviceName, group, request.getCallType());
		return loadBalance.select(availableClients, request, this);
	}

	private List<Client> filterWithGroupAndWeight(List<Client> clientList, String serviceName, String group, boolean isWriteBufferLimit) {
		Set<String> clientsInGroup = this.groupMap.get(group);
		if (clientsInGroup == null || clientsInGroup.isEmpty()) {
			throw new NoConnectionException("No group named[" + group + "].");
		}
		List<Client> filteredClients = new ArrayList<Client>(clientList.size());
		boolean existClientBuffToLimit = false;
		for (Client client : clientList) {
			String address = client.getAddress();
			if (client.isActive() && clientsInGroup.contains(address) && getWeightWithDefault(serviceName, address) > 0) {
				if (!isWriteBufferLimit || client.isWritable()) {
					filteredClients.add(client);
				} else {
					existClientBuffToLimit = true;
				}
			}
		}
		if (filteredClients.isEmpty()) {
			throw new NoConnectionException("No available server exists for service[" + serviceName + "] and group[" + group + "]" 
					+ (existClientBuffToLimit ? ", and exists some server's write buffer reach limit": "") + ".");
		}
		return filteredClients;
	}

	public void registerWeight(String serviceName,String group,String connect,int weight) {
		Set<String> connectSet = this.groupMap.get(group);
		if(connectSet == null){
			connectSet = new HashSet<String>();
			this.groupMap.put(group, connectSet);
		}
		connectSet.add(connect);
		
		Map<String,Integer> w = this.weights.get(serviceName);
		if(w == null){
			w = new ConcurrentHashMap<String,Integer>();
			this.weights.put(serviceName, w);
		}
		w.put(connect, weight);
		
		Map<String,Integer> w_ = this.weights_.get(serviceName);
		if(w_ == null){
			w_ = new ConcurrentHashMap<String,Integer>();
			this.weights_.put(serviceName, w_);
		}
		Integer value = w_.put(connect, weight);
//		if(value != null && value != weight){
//			throw new DPSFRuntimeException("the same service and the same host can not have many weight :"+weight+"-->>"+value);
//		}
		if(weight > 10){
			throw new DPSFRuntimeException("weight must be not over 10");
		}
	}
	
	public void doDefault(){
		Map<String,Map<String,Integer>> _weights = new ConcurrentHashMap<String,Map<String,Integer>>();
		for(Entry<String,Map<String,Integer>> we : this.weights_.entrySet()){
			Map<String,Integer> w = _weights.get(we.getKey());
			if(w == null){
				w = new ConcurrentHashMap<String,Integer>();
				_weights.put(we.getKey(), w);
			}
			for(Entry<String,Integer> subwe : we.getValue().entrySet()){
				w.put(subwe.getKey(), subwe.getValue());
			}
		}
		this.weights = _weights;
		this.weightCache = new ConcurrentHashMap<String,List<Integer>>();
	}
	
	public boolean setWeight(String address,int wt){
		boolean flag = false;
		for(Entry<String,Map<String,Integer>> we : this.weights.entrySet()){
			if(we.getValue().containsKey(address)){
				we.getValue().put(address, wt);
				flag = true;
			}
		}
		this.weightCache.clear();
		return flag;
	}
	
	/**
	 * 调整host对应所有服务的weight
	 * @param host
	 * @param wt
	 */
	public void setHostWeight(String host, int wt) {
		for(Entry<String,Map<String,Integer>> we : this.weights.entrySet()){
			for(Entry<String,Integer> connectWeightEntry : we.getValue().entrySet()) {
				String connect = connectWeightEntry.getKey();
				int colonIdx = connect.indexOf(":");
				String curHost = connect;
				if(colonIdx > 0) {
					curHost = curHost.substring(0, colonIdx);
				}
				if(curHost.equals(host)) {
					we.getValue().put(connect, wt);
				}
			}
		}
		this.weightCache.clear();
	}

	/**
	 * @return the weights
	 */
	public Map<String, Map<String, Integer>> getWeights() {
		return weights;
	}
	
	public Integer getWeight(String serviceName, String address) {
		Map<String, Integer> connectToWeight = weights.get(serviceName);
		Integer weight = null;
		if(connectToWeight != null) {
			weight = connectToWeight.get(address);
		}
		return weight;
	}

	@Override
	public int getWeightWithDefault(String serviceName, String address) {
		Integer weight = getWeight(serviceName, address);
		return weight != null ? weight : 1;
	}

}
