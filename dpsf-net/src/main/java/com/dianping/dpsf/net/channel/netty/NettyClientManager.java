/**
 * 
 */
package com.dianping.dpsf.net.channel.netty;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.util.ThreadNameDeterminer;
import org.jboss.netty.util.ThreadRenamingRunnable;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.component.DPSFCallback;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.Invoker;
import com.dianping.dpsf.exception.NetException;
import com.dianping.dpsf.exception.ServiceException;
import com.dianping.dpsf.jmx.DpsfRequestorMonitor;
import com.dianping.dpsf.jmx.ManagementContext;
import com.dianping.dpsf.net.channel.Client;
import com.dianping.dpsf.net.channel.config.ClusterConfigure;
import com.dianping.dpsf.net.channel.config.ConnectMetaData;
import com.dianping.dpsf.net.channel.config.HostInfo;
import com.dianping.dpsf.net.channel.manager.ClientCache;
import com.dianping.dpsf.net.channel.manager.ClientManager;
import com.dianping.dpsf.net.channel.manager.HeartBeatTask;
import com.dianping.dpsf.net.channel.manager.LionNotifier;
import com.dianping.dpsf.net.channel.manager.ReconnectTask;
import com.dianping.dpsf.net.channel.manager.RouteManager;
import com.dianping.dpsf.net.channel.manager.ServiceProviderChangeEvent;
import com.dianping.dpsf.net.channel.manager.ServiceProviderChangeListener;
import com.dianping.dpsf.stat.RpcStatsPool;
import com.dianping.dpsf.telnet.cmd.TelnetCommandState;
import com.dianping.dpsf.telnet.cmd.TelnetCommandWeight;
import com.dianping.dpsf.thread.CycThreadPool;
import com.dianping.dpsf.thread.DPSFThreadPool;
import com.dianping.dpsf.thread.DefaultThreadFactory;
import com.dianping.dpsf.thread.ExeThreadPool;
import com.dianping.lion.pigeon.PigeonClient;
import com.dianping.lion.pigeon.ServiceChange;

/**    
 * <p>    
 * Title: ClientManager.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-9 下午02:14:02   
 */
public class NettyClientManager implements ClientManager{
	
	private static Logger logger = DPSFLog.getLogger();
	
	public final static String DISABLE_DYNAMIC_SERVICE = "com.dianping.pigeon.dynamicService";
	
	public static final String LION_CLIENT_CLASS = "com.dianping.pigeon.lionClazz";
	
	public static String DEFAULT_LION_CLIENT_CLASS = "com.dianping.lion.pigeon.PigeonClientImpl";
	
	private final static int WEIGHT_DEFAULT = 1;
	
	private ClientCache clientCache;
	
	private HeartBeatTask heartBeatTask;
	private ReconnectTask reconnectTask;
	
	private RouteManager routerManager = new RouteManager();
	
	private Invoker invoker;
	
	private DPSFThreadPool clientResponseThreadPool;
	
	private Executor bossExecutor;
	private Executor workerExecutor;
	
	private Map<String, String> serviceNameToGroup = new HashMap<String, String>();
	private Map<String, Set<HostInfo>> serviceNameToHostInfos = new ConcurrentHashMap<String, Set<HostInfo>>();
	private PigeonClient lionPigeonClient;
	
	public NettyClientManager(){
		this.heartBeatTask = new HeartBeatTask(this, routerManager);
		this.reconnectTask = new ReconnectTask(this);
		this.clientCache = new ClientCache(this, this.heartBeatTask, this.reconnectTask);
		CycThreadPool.getPool().execute(this.heartBeatTask);
		CycThreadPool.getPool().execute(this.reconnectTask);
		ClusterConfigure.getInstance().addListener(this.clientCache);
		ClusterConfigure.getInstance().addListener(this.heartBeatTask);
		ClusterConfigure.getInstance().addListener(this.reconnectTask);
		TelnetCommandState.getInstance().setClientManager(this);
		TelnetCommandWeight.getInstance().setRoute(this.routerManager);
		//register requestor monitor to jmx server
		DpsfRequestorMonitor.getInstance().setClientManager(this);
		ManagementContext.getInstance().registerMBean(DpsfRequestorMonitor.getInstance());
		//TODO [v1.7.0, danson.liu]暂时使用CallerRunsPolicy策略, 1.7.0版中对dpsf中所有线程池进行统一规划
		this.clientResponseThreadPool = new ExeThreadPool("Client-ResponseProcessor", 50, 600,
				new ArrayBlockingQueue<Runnable>(50), new CallerRunsPolicy());	
		ThreadRenamingRunnable.setThreadNameDeterminer(ThreadNameDeterminer.CURRENT);	//Disable thread renaming of Netty
		this.bossExecutor = Executors.newCachedThreadPool(new DefaultThreadFactory("Netty-Client-BossExecutor"));
		this.workerExecutor = Executors.newCachedThreadPool(new DefaultThreadFactory("Netty-Client-WorkerExecutor"));
	
		LionNotifier.addListener(new ServiceProviderChangeListener() {
			
			@Override
			public void providerAdded(ServiceProviderChangeEvent event) {
				String group = serviceNameToGroup.get(event.getServiceName());
				if(group == null) {
					logger.error("can not map serviceName=" + event.getServiceName() + " to group");
					return;
				}
				logger.info("add " + event.getHost() + ":" + event.getPort() + " to " + event.getServiceName());
				registeClient(event.getServiceName(), group, event.getHost() + ":" + event.getPort(), event.getWeight());
			}
			
			@Override
			public void providerRemoved(ServiceProviderChangeEvent event) {
				Set<HostInfo> hostInfoSet = serviceNameToHostInfos.get(event.getServiceName());
				if(hostInfoSet != null) {
					hostInfoSet.remove(new HostInfo(event.getHost(), event.getPort(), event.getWeight()));
				}
			}
			
			@Override
			public void hostWeightChanged(ServiceProviderChangeEvent event) {
			}
		});
		
		initLionPigeonClient();
		
	}

	private void initLionPigeonClient() {
		String dynamicService = System.getProperty(DISABLE_DYNAMIC_SERVICE);
		String lionClientClass = System.getProperty(LION_CLIENT_CLASS, DEFAULT_LION_CLIENT_CLASS);
		if("true".equalsIgnoreCase(dynamicService)) {
			logger.info("disabled by system property, start without dynamic service support");
		} else {
			Class clazz = null;
			try {
				clazz = Class.forName(lionClientClass);
			} catch (Exception e) {
				logger.info("lion not found, start without dynamic service support");
			}
			if(clazz != null) {
				logger.info("found lion, start with dynamic service support");
				try {
					ServiceChange sc = new ServiceChange() {
						
						@Override
						public synchronized void onServiceHostChange(String serviceName, List<String[]> hostList) {
							try {
								Set<HostInfo> newHpSet = parseHostPortList(serviceName, hostList);
								Set<HostInfo> oldHpSet = serviceNameToHostInfos.get(serviceName);
								Set<HostInfo> toAddHpSet = Collections.EMPTY_SET;
								Set<HostInfo> toRemoveHpSet = Collections.EMPTY_SET;
								if(oldHpSet == null) {
									toAddHpSet = newHpSet;
								} else {
									toRemoveHpSet = new HashSet(oldHpSet);
									toRemoveHpSet.removeAll(newHpSet);
									toAddHpSet = new HashSet(newHpSet);
									toAddHpSet.removeAll(oldHpSet);
								}
								for (HostInfo hostPort : toAddHpSet) {
									LionNotifier.providerAdded(serviceName, hostPort.getHost(), hostPort.getPort(), hostPort.getWeight());
								}
								for (HostInfo hostPort : toRemoveHpSet) {
									LionNotifier.providerRemoved(serviceName, hostPort.getHost(), hostPort.getPort());
								}
							}catch (Exception e) {
								logger.error("error change service host", e);
							}
						}
						
						private Set<HostInfo> parseHostPortList(String serviceName, List<String[]> hostList) {
							Set<HostInfo> hpSet = new HashSet<HostInfo>();
							if(hostList != null) {
								for (String[] parts : hostList) {
									String host = parts[0];
									String port = parts[1];
									String connect = host + ":" + port;
									Integer weight = routerManager.getWeight(serviceName, connect);
									if(weight == null) {
										try {
											weight = lionPigeonClient.getHostWeigth(connect);
										} catch (Exception e) {
											logger.error("error get weight from PigeonClient for host " + host + ", use default weight" , e);
											weight = WEIGHT_DEFAULT;
										}
									}
									hpSet.add(new HostInfo(host, Integer.parseInt(port), weight));
								}
							}
							return hpSet;
						}

						@Override
						public synchronized void onHostWeightChange(String connect, int weight) {
							logger.info("setting weight of " + connect + " to " + weight);
							int colonIdx = connect.indexOf(":");
							String host = connect.substring(0, colonIdx);
							int port = Integer.parseInt(connect.substring(colonIdx + 1));
							LionNotifier.hostWeightChanged(host, port, weight);
						}
					};
					Constructor con = clazz.getConstructor(new Class[]{ServiceChange.class});
					lionPigeonClient = (PigeonClient) con.newInstance(sc);
					logger.info("successfully create Lion's PigeonClient of class " + clazz.getName());
				} catch (Exception e) {
					logger.error("error create Lion's PiegonClient", e);
					throw new RuntimeException(e);
				}
			}
		}
	}

	public synchronized void registeClient(String serviceName,String group,String connect,int weight) {
		ClusterConfigure.getInstance().addConnect(new ConnectMetaData(serviceName,connect,weight));
		this.routerManager.registerWeight(serviceName,group,connect, weight);
		
		//TODO: 暂不支持一个serviceName对应多个group
		serviceNameToGroup.put(serviceName, group);
		Set<HostInfo> hpSet = serviceNameToHostInfos.get(serviceName);
		HostInfo hostInfo = new HostInfo(connect, weight);
		if(hpSet == null) {
			hpSet = new HashSet<HostInfo>();
			hpSet.add(hostInfo);
			serviceNameToHostInfos.put(serviceName, hpSet);
		} else {
			hpSet.add(hostInfo);
		}
	}
	
	public Client getClient(String serviceName,String group, DPSFRequest request) throws NetException{
		List<Client> clientList = clientCache.getClientList(serviceName);
		return routerManager.route(clientList,serviceName,group, request);	
	}
	//TODO 消息发送线程驱动，后期可以优化
	public void connectionException(Client client,Object attachment,ExceptionEvent e){
//		if(!client.isConnected())
//			ClusterConfigure.getInstance().removeConnect(client.getHost()+ConnectMetaData.PLACEHOLDER+client.getPort());
		if(attachment == null){
			return;
		}
		Object[] msg = (Object[])attachment;
		if(msg[0] instanceof DPSFRequest 
				&& ((DPSFRequest) msg[0]).getMessageType() == Constants.MESSAGE_TYPE_SERVICE
				&& msg[1] != null){
			logger.error(e.getCause().getMessage(),e.getCause());
			try{
				DPSFRequest request = (DPSFRequest)msg[0];
				DPSFCallback callback = (DPSFCallback)msg[2];
				
				//TODO [v1.7.0, danson.liu]重发目前实现有问题, 另外还需要考虑重发次数, 不能无限重发!
				Client client_ = getClient(request.getServiceName(),(String)msg[3], request);
				
				if(client != null){
					RpcStatsPool.flowIn(request, client.getAddress());
					//TODO 目前future的concel有些问题，后期处理
					client.write(request, callback);
				}else{
					logger.error("no client for use to "+request.getServiceName());
				}
			}catch(NetException ne){
				logger.error(ne.getMessage(),ne);
			}
		}
		
	}
	
	public void processResponse(DPSFResponse response,Client client) throws ServiceException{
		if(response.getMessageType() == Constants.MESSAGE_TYPE_HEART){
			this.heartBeatTask.processResponse(response,client);
		}else{
			this.invoker.invokeReponse(response);
		}
		
	}

	/**
	 * @param invoker the invoker to set
	 */
	public void setInvoker(Invoker invoker) {
		this.invoker = invoker;
	}

	/**
	 * @return the clientCache
	 */
	public ClientCache getClientCache() {
		return clientCache;
	}

	/**
	 * @return the heartTask
	 */
	public HeartBeatTask getHeartTask() {
		return heartBeatTask;
	}
	
	public ReconnectTask getReconnectTask() {
		return reconnectTask;
	}

	/**
	 * @return the clientResponseThreadPool
	 */
	public DPSFThreadPool getClientResponseThreadPool() {
		return clientResponseThreadPool;
	}

	/**
	 * @return the bossExecutor
	 */
	public Executor getBossExecutor() {
		return bossExecutor;
	}

	/**
	 * @return the workerExecutor
	 */
	public Executor getWorkerExecutor() {
		return workerExecutor;
	}

	/**
	 * @param clientResponseThreadPool the clientResponseThreadPool to set
	 */
	public void setClientResponseThreadPool(DPSFThreadPool clientResponseThreadPool) {
		this.clientResponseThreadPool = clientResponseThreadPool;
	}

	/**
	 * 用Lion从ZK中获取serviceName对应的服务地址，并注册这些服务地址
	 */
	@Override
	public synchronized void findAndRegisterClientFor(String serviceName, String group) {
		if(lionPigeonClient != null) {
			String addressStr = null;
			try {
				addressStr = lionPigeonClient.getServiceAddress(serviceName);
			} catch (Exception e) {
				logger.error("error get service client info from lion for serviceName=" + serviceName, e);
				throw new RuntimeException(e);
			}
			serviceNameToGroup.put(serviceName, group);
			if(addressStr != null) {
				addressStr = addressStr.trim();
				String[] addressList = addressStr.split(",");
				for (int i = 0; i < addressList.length; i++) {
					if (StringUtils.isNotBlank(addressList[i])) {
						String[] parts = addressList[i].split(":");
						String host = parts[0];
						int port = Integer.parseInt(parts[1]);
						Integer weight = routerManager.getWeight(serviceName, addressList[i]);
						if(weight == null) {
							try {
								weight = lionPigeonClient.getHostWeigth(addressList[i]);
							} catch (Exception e) {
								throw new RuntimeException("error get host weight from ", e);
							}
						}
						LionNotifier.providerAdded(serviceName, host, port, weight);
					}
				}
			}
		} else {
			throw new RuntimeException("no client found for serviceName=" + serviceName + ", and lion is not found or disabled");
		}
	}


	@Override
	public synchronized void setGroupRoute(String serviceName, String group, Set<String> connectSet) {
		if(connectSet != null ){
			for (String connect : connectSet) {
				Set<HostInfo> hostInfoSet = serviceNameToHostInfos.get(serviceName);
				if(hostInfoSet == null || !hostInfoSet.contains(new HostInfo(connect, 1))) {
					registeClient(serviceName, group, connect, 1);	//weight始终为1
				}
			}
		}
		//TODO close client?
		routerManager.setGroupRoute(group, connectSet);
	}

	@Override
	public Map<String, Set<HostInfo>> getServiceHostInfos() {
		return serviceNameToHostInfos;
	}

	public RouteManager getRouterManager() {
		return routerManager;
	}
	
}
