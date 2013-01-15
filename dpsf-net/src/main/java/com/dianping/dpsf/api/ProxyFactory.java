package com.dianping.dpsf.api;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.dianping.dpsf.component.ClusterMetaParser;
import com.dianping.dpsf.component.impl.DefaultClusterMetaParser;
import com.dianping.dpsf.exception.DPSFException;
import com.dianping.dpsf.invoke.RemoteInvocationHandlerFactory;
import com.dianping.dpsf.invoke.filter.InvocationInvokeFilter;
import com.dianping.dpsf.spring.PigeonBootStrap;
import org.apache.log4j.Logger;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.async.ServiceCallback;
import com.dianping.dpsf.component.DPSFMetaData;
import com.dianping.dpsf.control.PigeonConfig;
import com.dianping.dpsf.exception.NetException;
import com.dianping.dpsf.net.channel.cluster.LoadBalance;
import com.dianping.dpsf.net.channel.manager.ClientManagerFactory;
import com.dianping.dpsf.net.channel.manager.LoadBalanceManager;
import com.dianping.dpsf.net.channel.netty.NettyClientManager;
import com.dianping.dpsf.invoke.ProxyInvoker;

public class ProxyFactory<IFACE>{

	private static Logger logger = DPSFLog.getLogger();

	private static AtomicInteger groupId = new AtomicInteger(0);

	private String serviceName;

	private Class<?> iface;

	private String serialize = Constants.SERIALIZE_HESSIAN;

	private String callMethod = Constants.CALL_SYNC;

	private String hosts;

	private String weight;

	private int timeout = 2000;

	private IFACE obj;

	private ServiceCallback callback;

	private String group;

	private boolean useLion = false;

	/**
	 * 是否对写Buffer限制大小(对于channel使用到的queue buffer的大小限制, 避免OutOfMemoryError)
	 */
	private boolean writeBufferLimit = PigeonConfig.getDefaultWriteBufferLimit();

	private String loadBalance;
	private Class<? extends LoadBalance> loadBalanceClass;
	private LoadBalance loadBalanceObj;
    private Map<InvocationInvokeFilter.InvokePhase, List<InvocationInvokeFilter>> customizedInvocationFilters;
    private Map<String, String> clusterConfig;
    private ClusterMetaParser clusterMetaParser = new DefaultClusterMetaParser();

    public void init() {
        PigeonBootStrap.setupClient();
		if(this.group == null){
			this.group = this.iface.getName()+"_"+groupId.incrementAndGet();
		}

		if(Constants.SERIALIZE_JAVA.equalsIgnoreCase(this.serialize)){
			initJavaAndHessian();
		}else if(Constants.SERIALIZE_HESSIAN.equalsIgnoreCase(this.serialize)){
			initJavaAndHessian();
		}else {
            throw new DPSFException("Only hessian and java serialize type supported!");
		}

		configLoadBalance();

		if (!this.useLion) {
			String[] hostArray = this.hosts.split(",");
			int[] weightArray = new int[hostArray.length];
			if(this.weight != null && this.weight.length() > 0){
				String[] weightStrArray = this.weight.trim().split(",");
				if(weightStrArray.length != hostArray.length){
					throw new NetException("hosts length must equal to weight length");
				}
				for(int i=0;i<weightArray.length;i++){
					weightArray[i] = Integer.parseInt(weightStrArray[i]);
					if(weightArray[i] > 10 || weightArray[i] <=0 ){
						throw new NetException("weight number can not be greater than 10 and less than 1:"+this.weight);
					}
				}
			}else{
				for(int i=0;i<weightArray.length;i++){
					weightArray[i] = 1;
				}
			}
			System.setProperty(NettyClientManager.DISABLE_DYNAMIC_SERVICE, "true");	//禁用动态服务
			for(int i=0;i<hostArray.length;i++){
				try {
					ClientManagerFactory.getClientManager().registeClient(
							this.serviceName,this.group, hostArray[i],weightArray[i]);
				} catch (NetException e) {
					logger.error(e.getMessage(),e);
				}
			}
		} else {
			logger.info("host list is not set, try to fetch from ZK");
			ClientManagerFactory.getClientManager().findAndRegisterClientFor(serviceName, group);
		}

	}

	private void configLoadBalance() {
		Object loadBalanceToSet = loadBalanceObj != null ? loadBalanceObj : (loadBalanceClass != null ? loadBalanceClass : (loadBalance != null ? loadBalance : null));
		if (loadBalanceToSet != null) {
			LoadBalanceManager.register(serviceName, group, loadBalanceToSet);
		}
	}

	public void setGroupRoute(String serviceName, String group, Set<String> connectSet) {
		ClientManagerFactory.getClientManager().setGroupRoute(serviceName, group, connectSet);
	}

	private void initJavaAndHessian() {
        DPSFMetaData metadata = new DPSFMetaData(this.serviceName, this.timeout, this.callMethod, this.serialize,
                this.callback, this.group, this.writeBufferLimit);
        metadata.setClusterMeta(clusterMetaParser.parse(clusterConfig));
        this.obj = (IFACE)Proxy.newProxyInstance(ProxyFactory.class.getClassLoader(), new Class[]{this.iface},
                new ProxyInvoker(metadata, RemoteInvocationHandlerFactory.createInvokeHandler(customizedInvocationFilters)));
	}

	public IFACE getProxy() throws Exception {
		return this.obj;
	}

	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * @param callMethod the callMethod to set
	 */
	public void setCallMethod(String callMethod) {
		this.callMethod = callMethod;
	}

	/**
	 * @param hosts the hosts to set
	 */
	public void setHosts(String hosts) {
		this.hosts = hosts;
	}

	/**
	 * @param timeout the timeout to set
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}


	/**
	 * @param iface the iface to set
	 */
	public void setIface(Class iface) {
		this.iface = iface;
	}

	/**
	 * @param serialize the serialize to set
	 */
	public void setSerialize(String serialize) {
		this.serialize = serialize;
	}

	/**
	 * @return the weight
	 */
	public String getWeight() {
		return weight;
	}

	/**
	 * @param weight the weight to set
	 */
	public void setWeight(String weight) {
		this.weight = weight;
	}

	/**
	 * @return the callback
	 */
	public ServiceCallback getCallback() {
		return callback;
	}

	/**
	 * @param callback the callback to set
	 */
	public void setCallback(ServiceCallback callback) {
		this.callback = callback;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	public void setLoadBalance(String loadBalance) {
		this.loadBalance = loadBalance;
	}

	public void setLoadBalanceClass(Class<? extends LoadBalance> loadBalanceClass) {
		this.loadBalanceClass = loadBalanceClass;
	}

	public void setLoadBalanceObj(LoadBalance loadBalanceObj) {
		this.loadBalanceObj = loadBalanceObj;
	}

	public void setUseLion(boolean useLion) {
		this.useLion = useLion;
	}

	public void setWriteBufferLimit(boolean writeBufferLimit) {
		this.writeBufferLimit = writeBufferLimit;
	}

    public void setCustomizedInvocationFilters(Map<InvocationInvokeFilter.InvokePhase, List<InvocationInvokeFilter>> customizedInvocationFilters) {
        this.customizedInvocationFilters = customizedInvocationFilters;
    }

    public void setClusterConfig(Map<String, String> clusterConfig) {
        this.clusterConfig = clusterConfig;
    }
}
