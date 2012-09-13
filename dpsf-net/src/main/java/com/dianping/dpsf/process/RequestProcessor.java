/**
 * 
 */
package com.dianping.dpsf.process;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import com.dianping.cat.Cat;
import com.dianping.dpsf.Constants;
import com.dianping.dpsf.ContextUtil;
import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.exception.DPSFTimeoutException;
import com.dianping.dpsf.jmx.DpsfResponsorMonitor;
import com.dianping.dpsf.jmx.ManagementContext;
import com.dianping.dpsf.net.channel.DPSFChannel;
import com.dianping.dpsf.repository.ServiceRepository;
import com.dianping.dpsf.telnet.cmd.TelnetCommandState;
import com.dianping.dpsf.thread.CycThreadPool;
import com.dianping.dpsf.thread.DPSFThreadPool;
import com.dianping.dpsf.thread.ExeThreadPool;

/**    
 * <p>    
 * Title: RequestProcesser.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-26 下午06:46:42   
 */
public class RequestProcessor {
	
	private static Logger logger = DPSFLog.getLogger();
	private static Logger log = Logger.getLogger(RequestProcessor.class);
	
	private ChannelGroup serverChannels;
	
	private DPSFThreadPool threadPool;
	
	private Map<DPSFRequest,RequestContext> contexts;
	
	private ServiceRepository sr;
	
	private int port;
	
	private ExecutorListener listener = new ExecutorListener(){

		public void executorCompleted(DPSFRequest request) {
			contexts.remove(request);
		}
		
	};
	
	public RequestProcessor(ServiceRepository sr,int port,int corePoolSize,int maxPoolSize,int workQueueSize){
		this.sr = sr;
		this.port = port;
		this.serverChannels = new DefaultChannelGroup("Server-Channels");
		this.threadPool = new ExeThreadPool("Server-RequestProcessor-"+this.port,corePoolSize,maxPoolSize,new LinkedBlockingQueue<Runnable>(workQueueSize));
		
		this.contexts = new ConcurrentHashMap<DPSFRequest,RequestContext>();
		CycThreadPool.getPool().execute(new TimeoutCheck());
		TelnetCommandState.getInstance().addRequestProcessor(this);
		//register responsor monitor to jmx server
		DpsfResponsorMonitor.getInstance().addRequestProcessor(this);
		ManagementContext.getInstance().registerMBean(DpsfResponsorMonitor.getInstance());
	}
	
	public void addChannel(DPSFChannel channel) {
		Channel channel_ = null;
		this.serverChannels.add(channel.getChannel(channel_));
	}
	public void addRequest(DPSFRequest request,Channel channel){
		RequestExecutor executor = new RequestExecutor(request,channel,this.sr,this);
		executor.addListener(this.listener);
		RequestContext context = new RequestContext(((InetSocketAddress)channel.getRemoteAddress()).getHostName());
		//必须新放入context，不然线程执行时找不到此context
		this.contexts.put(request, context);
		context.setFuture(this.threadPool.submit(executor));
	}
	
	private void fail(DPSFRequest request){
		
	}
	
	void putThread(DPSFRequest request,Thread thread){
		RequestContext rc = this.contexts.get(request);
		if(rc != null){
			rc.setThread(thread);
		}else{
			logger.error("no context for this request and thread:seq::"+request.getSequence());
		}
	}
	
	public class TimeoutCheck implements Runnable{

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			while(true){
				try{
					long currentTime = System.currentTimeMillis();
					for(DPSFRequest request : contexts.keySet()){
						if(request.getCreateMillisTime()+request.getTimeout() < currentTime){
							try{
								RequestContext rc = contexts.get(request);
								if (request.getMessageType() == Constants.MESSAGE_TYPE_HEART) {
									Future future = rc.getFuture();
									if (future != null) {
										future.cancel(false);
									}
								} else {
									//记录超时堆栈
									DPSFTimeoutException te;
									StringBuffer msg = new StringBuffer();
									msg.append("DPSF RequestExecutor timeout seq:").append(request.getSequence());
									msg.append("  ip:").append(rc.getHost()).append("  timeout:"+request.getTimeout())
									.append("  createTime:").append(request.getCreateMillisTime())
									.append("\r\n");
									Object[] params = request.getParameters();
									if(params != null && params.length > 0){
										for(Object param : params){
											msg.append("<><>").append(String.valueOf(param));
										}
										msg.append("\r\n");
									}
									Thread t = rc.getThread();
									if(t == null){
										msg.append(" and task has been not executed by threadPool");
										
										te = new DPSFTimeoutException(msg.toString());
									}else{
										te = new DPSFTimeoutException(msg.toString());
										te.setStackTrace(t.getStackTrace());
									}
									ContextUtil.setContext(request.getContext());
									logger.error(te.getMessage(),te);
									log.error(te.getMessage(),te);
									Cat.getProducer().logError(te);
									
									Future fu = rc.getFuture();
									if(fu != null){
										fu.cancel(false);
									}else{
										log.error("<<<<<< No Future for Request  \r\n"+msg.toString());
									}
								}
							}finally{
								contexts.remove(request);
							}
							fail(request);
							RequestExecutor.requestStat.failCountService(request.getServiceName());
						}
					}
				}catch(Exception e){
					log.error(e.getMessage(),e);
				} finally {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {}
				}
			}
		}
		
	}

	/**
	 * @return the threadPool
	 */
	public DPSFThreadPool getThreadPool() {
		return threadPool;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return the serverChannels
	 */
	public ChannelGroup getServerChannels() {
		return serverChannels;
	}

	public ServiceRepository getServiceRepository() {
		return sr;
	}

}
