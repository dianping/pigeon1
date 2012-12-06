package com.dianping.dpsf.api;

import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

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
import com.dianping.dpsf.spring.ProxyInvoker;

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
	
	public void init() {
		
		if(this.group == null){
			this.group = this.iface.getName()+"_"+groupId.incrementAndGet();
		}
		
		if(Constants.SERIALIZE_PB.equalsIgnoreCase(this.serialize)){
			initPB();
		}else if(Constants.SERIALIZE_JAVA.equalsIgnoreCase(this.serialize)){
			initJavaAndHessian();
		}else if(Constants.SERIALIZE_HESSIAN.equalsIgnoreCase(this.serialize)){
			initJavaAndHessian();
		}else if(Constants.SERIALIZE_THRIFT.equalsIgnoreCase(this.serialize)){
			initThrift();
		}else if(Constants.SERIALIZE_WS.equals(this.serialize)){
			initWS();
			return;
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
	
	private void initPB() {
//		if(this.callMethod.equalsIgnoreCase(Constants.CALL_CALLBACK)){
//			if(this.iface.endsWith("$Interface")){
//				this.interfaceName = this.iface;
//				this.iface = this.iface.substring(0,this.iface.lastIndexOf("$Interface"));
//			}else{
//				this.interfaceName = this.iface+"$Interface";
//			}
//			this.stubName = this.iface+"$Stub";
//			this.channelClass = com.google.protobuf.RpcChannel.class;
//			this.channel = new DPSFRpcChannel(new DPSFMetaData(this.serviceName,this.timeout,this.group));
//		}else if(this.callMethod.equalsIgnoreCase(Constants.CALL_SYNC)){
//			if(this.iface.endsWith("$BlockingInterface")){
//				this.interfaceName = this.iface;
//				this.iface = this.iface.substring(0,this.iface.lastIndexOf("$BlockingInterface"));
//			}else{
//				this.interfaceName = this.iface+"$BlockingInterface";
//			}
//			
//			this.stubName = this.iface+"$BlockingStub";
//			this.channelClass = com.google.protobuf.BlockingRpcChannel.class;
//			this.channel = new DPSFBlockingRpcChannel(new DPSFMetaData(this.serviceName,this.timeout,this.group));
//		}
//		
//		this.objType = Class.forName(this.stubName);
//		Constructor constructor = this.objType.getDeclaredConstructor(this.channelClass);
//		constructor.setAccessible(true);
//		this.obj = constructor.newInstance(this.channel);
	}
	
	private void initJavaAndHessian() {
		this.obj = (IFACE)Proxy.newProxyInstance(ProxyFactory.class.getClassLoader(), 
				new Class[]{this.iface}, new ProxyInvoker(new DPSFMetaData(this.serviceName,
						this.timeout,this.callMethod,this.serialize,this.callback,this.group, this.writeBufferLimit)));
	}
	
//	private void initWS() throws Exception{
//		this.objType = Class.forName(this.iface);
//		String serviceURL = "";
//		if(this.hosts != null && this.hosts.length() > 0){
//			if(!this.hosts.endsWith("/") && !this.serviceName.startsWith("/")){
//				serviceURL = this.hosts+"/"+this.serviceName;
//			}else{
//				serviceURL = this.hosts + this.serviceName;
//			}
//		}
//		if(!serviceURL.startsWith("http")){
//			serviceURL = "http://"+serviceURL;
//		}
////		Service serviceModel = new ObjectServiceFactory(
////				new AegisBindingProvider(new JaxbTypeRegistry())).create(this.objType); 
////        XFireProxyFactory serviceFactory = new XFireProxyFactory();
//		Service serviceModel = new JaxbServiceFactory().create(this.objType);
//        XFire xfire = XFireFactory.newInstance().getXFire();
//       
//        XFireProxyFactory factory = new XFireProxyFactory(xfire); 
//        this.obj = factory.create(serviceModel, serviceURL);
//        Client client = Client.getInstance(this.obj);
////        client.addOutHandler(new OutHeaderHandler());
//        client.setProperty(CommonsHttpMessageSender.HTTP_TIMEOUT, this.timeout+"");
//	}
	
	private void initWS(){
//		this.objType = Class.forName(this.iface);
////		String serviceURL = "";
////		if(this.hosts != null && this.hosts.length() > 0){
////			if(!this.hosts.endsWith("/") && !this.serviceName.startsWith("/")){
////				serviceURL = this.hosts+"/"+this.serviceName;
////			}else{
////				serviceURL = this.hosts + this.serviceName;
////			}
////		}else{
////			serviceURL = this.serviceName;
////		}
//		if(!this.serviceName.startsWith("http")){
//			if(this.serviceName.startsWith("//")){
//				this.serviceName = "http:"+this.serviceName;
//			}else if(this.serviceName.startsWith("/")){
//				this.serviceName = "http:/"+this.serviceName;
//			}else{
//				this.serviceName = "http://"+this.serviceName;
//			}
//		}
//		this.serviceName = this.serviceName.toLowerCase();
//		if(!this.serviceName.endsWith("?wsdl")){
//			throw new NetException("serviceName must be endWith \"?wsdl\"");
//		}
//		this.obj = Proxy.newProxyInstance(ProxyBeanFactory.class.getClassLoader(), 
//				new Class[]{this.objType}, new WSProxyInvoker(this.objType,this.serviceName,this.timeout));
	}
	
	private void initThrift() {
//		if(!this.iface.endsWith("$Iface")){
//			this.iface += "$iface";
//		}
//		this.objType = Class.forName(this.iface);
//		this.obj = Proxy.newProxyInstance(ProxyBeanFactory.class.getClassLoader(), 
//				new Class[]{this.objType}, new ThriftProxyInvoker(this.iface,
//						new DPSFMetaData(this.serviceName,this.timeout,this.callMethod,this.serialize,this.callback,this.group)));
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
	
}
