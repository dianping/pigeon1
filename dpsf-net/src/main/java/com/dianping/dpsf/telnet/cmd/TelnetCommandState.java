/**
 * 
 */
package com.dianping.dpsf.telnet.cmd;

import java.lang.Thread.State;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadPoolExecutor;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;

import com.dianping.dpsf.net.channel.Client;
import com.dianping.dpsf.net.channel.netty.NettyClientManager;
import com.dianping.dpsf.process.RequestProcessor;
import com.dianping.dpsf.telnet.TelnetCommandExecutor;

/**    
 * <p>    
 * Title: TelnetCommandState.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2011-3-1 上午11:47:06   
 */
public class TelnetCommandState implements TelnetCommandExecutor{
	
	private static TelnetCommandState state = new TelnetCommandState();
	
	private NettyClientManager clientManager;
	private Map<Integer,RequestProcessor> requestProcessorMap = new HashMap<Integer,RequestProcessor>();
	
	private String cmd = "-state";
	private String desc = "state DPSF connect and thread state\r\n" +
			"        example:-state tstack           (for 1 runnable stack)\r\n" +
			"                -state tstack 5         (for 5 runnable stack)\r\n" +
			"                -state tstack blocked   (for 1 blocked stack)\r\n" +
			"                -state tstack blocked 4 (for 4 blocked stack)";
	
	public static TelnetCommandState getInstance(){
		return state;
	}
	
	public String execute(String[] cmds) {
		if(cmds.length == 1){
			return stateExe();
		}else{
			return subStateExe(cmds);
		}
	}
	
	private String stateExe(){
		StringBuffer sb = new StringBuffer();
		sb.append("********** client state ************\r\n");
		if(this.clientManager != null){
			sb.append("\r\n");
			sb.append("*** thread--state ***\r\n");
			ThreadPoolExecutor executor = this.clientManager.getClientResponseThreadPool().getExecutor();
			sb.append("poolSize:").append(executor.getPoolSize()).append("\r\n");
			sb.append("activeCount:").append(executor.getActiveCount()).append("\r\n");
			sb.append("waitTaskCount:").append(executor.getQueue().size()).append("\r\n");
			countThreadState(this.clientManager.getClientResponseThreadPool().getFactory().getGroup(),sb);
			sb.append("\r\n");
			sb.append("*** client--connected ***\r\n");
			for(Entry<String,Client> connect : this.clientManager.getClientCache().getAllClients().entrySet()){
				if(!this.clientManager.getReconnectTask().getClosedClients().containsKey(connect.getKey())){
					InetSocketAddress isa = (InetSocketAddress)connect.getValue().getChannel().getLocalAddress();
					
					sb.append(isa.getHostName()).append(":").append(isa.getPort());
					sb.append("-->>");
					sb.append(connect.getKey());
					sb.append("\r\n");
				}
			}
			sb.append("\r\n");
			sb.append("*** client--disconnected ***\r\n");
			for(Entry<String,Client> connect : this.clientManager.getReconnectTask().getClosedClients().entrySet()){
				sb.append(connect.getKey());
				sb.append("\r\n");
			}
		}else{
			sb.append("client is not init\r\n");
		}
		sb.append("\r\n");
		sb.append("*********** Server state ***********\r\n");
		if(this.requestProcessorMap.size() > 0){
			for(Entry<Integer,RequestProcessor> entry : this.requestProcessorMap.entrySet()){
				sb.append("\r\nprocessor:").append(entry.getKey()).append(">>>>:\r\n");
				RequestProcessor requestProcessor = entry.getValue();
				sb.append("*** server--connected ***\r\n");
				ChannelGroup cg = requestProcessor.getServerChannels();
				Iterator<Channel> ic = cg.iterator();
				while(ic.hasNext()){
					Channel c = ic.next();
					InetSocketAddress risa = (InetSocketAddress)c.getRemoteAddress();
					InetSocketAddress lisa = (InetSocketAddress)c.getLocalAddress();
					sb.append(risa.getHostName()).append(":").append(risa.getPort())
					.append("-->>").append(lisa.getHostName()).append(":")
					.append(lisa.getPort()).append("\r\n");
				}
				sb.append("\r\n");
				sb.append("*** thread--state ***\r\n");
				ThreadPoolExecutor executor = requestProcessor.getThreadPool().getExecutor();
				sb.append("poolSize:").append(executor.getPoolSize()).append("\r\n");
				sb.append("activeCount:").append(executor.getActiveCount()).append("\r\n");
				sb.append("waitTaskCount:").append(executor.getQueue().size()).append("\r\n");
				countThreadState(requestProcessor.getThreadPool().getFactory().getGroup(),sb);
			}
			
		}else{
			sb.append("processor is not init\r\n");
		}
		return sb.toString();
	}
	
	private void countThreadState(ThreadGroup tg,StringBuffer sb){
		Thread[] threads = new Thread[tg.activeCount()];
		tg.enumerate(threads,false);
		int newCount = 0;
		int runnableCount = 0;
		int blockedCount = 0;
		int waitingCount = 0;
		int timed_waitingCount = 0;
		int terminatedCount = 0;
		for(Thread t : threads){
			State s = t.getState();
			switch(s){
			case NEW:
				newCount++;
				break;
			case RUNNABLE:
				runnableCount++;
				break;
			case BLOCKED:
				blockedCount++;
				break;
			case WAITING:
				waitingCount++;
				break;
			case TIMED_WAITING:
				timed_waitingCount++;
				break;
			case TERMINATED:
				terminatedCount++;
				break;
			}
		}
		sb.append("threadState-new-Count:").append(newCount).append("\r\n");
		sb.append("threadState-runnable-Count:").append(runnableCount).append("\r\n");
		sb.append("threadState-blocked-Count:").append(blockedCount).append("\r\n");
		sb.append("threadState-waiting-Count:").append(waitingCount).append("\r\n");
		sb.append("threadState-timed_waiting-Count:").append(timed_waitingCount).append("\r\n");
		sb.append("threadState-terminated-Count:").append(terminatedCount).append("\r\n");
	}
	
	private String subStateExe(String[] cmds){
		StringBuffer sb = new StringBuffer();
		if(cmds.length >= 2 && "tstack".equalsIgnoreCase(cmds[1])){
			
			int stackCount = 1;
			String stackType = "runnable";
			State state = State.RUNNABLE;
			if(cmds.length == 3){
				try{
					stackCount = Integer.parseInt(cmds[2]);
				}catch(Exception e){
					stackType = cmds[2];
				}
			}else if(cmds.length >= 4){
				stackType = cmds[2];
				try{
					stackCount = Integer.parseInt(cmds[3]);
				}catch(Exception e){
					sb.append("stackCount must be int\r\n");
					return sb.toString();
				}
			}
			if("new".equalsIgnoreCase(stackType)){
				state = State.NEW;
			}else if("RUNNABLE".equalsIgnoreCase(stackType)){
				state = State.RUNNABLE;
			}else if("BLOCKED".equalsIgnoreCase(stackType)){
				state = State.BLOCKED;
			}else if("WAITING".equalsIgnoreCase(stackType)){
				state = State.WAITING;
			}else if("TIMED_WAITING".equalsIgnoreCase(stackType)){
				state = State.TIMED_WAITING;
			}else if("TERMINATED".equalsIgnoreCase(stackType)){
				state = State.TERMINATED;
			}else{
				sb.append("parameter is error:"+stackType).append("\r\n");
			}
			
			if(this.requestProcessorMap.size() > 0){
				for(Entry<Integer,RequestProcessor> entry : this.requestProcessorMap.entrySet()){
					sb.append("\r\nprocessor:").append(entry.getKey()).append(">>>>:\r\n");
					RequestProcessor requestProcessor = entry.getValue();
					ThreadPoolExecutor executor = requestProcessor.getThreadPool().getExecutor();
					ThreadGroup tg = requestProcessor.getThreadPool().getFactory().getGroup();
					Thread[] threads = new Thread[tg.activeCount()];
					tg.enumerate(threads,false);
					int i = 0;
					if(threads != null && threads.length > 0){
						for(Thread t : threads){
							if(state == t.getState()){
								i++;
								if(i>1){
									sb.append("\r\n\r\n");
								}
								sb.append("Thread ").append(t.getId()).append("  ").append(t.getName());
								sb.append(" (state = ").append(stackType).append(")").append("\r\n\r\n");
								StackTraceElement[] stes = t.getStackTrace();
								for(StackTraceElement ste : stes){
									sb.append(ste.getClassName()).append("-").append(ste.getMethodName());
									sb.append("(").append(ste.getLineNumber()).append(")").append("\r\n");
								}
							}
							if(i >= stackCount){
								break;
							}
						}
					}else{
						sb.append("no thread \r\n");
					}
				}
				
			}else{
				sb.append("processor is not init\r\n");
			}
			
			
		}else{
			sb.append("parameter is error\r\n");
		}
		return sb.toString();
	}

	/**
	 * @return the clientManager
	 */
	public NettyClientManager getClientManager() {
		return clientManager;
	}

	/**
	 * @param clientManager the clientManager to set
	 */
	public void setClientManager(NettyClientManager clientManager) {
		this.clientManager = clientManager;
	}


	/**
	 * @param requestProcessor the requestProcessor to set
	 */
	public void addRequestProcessor(RequestProcessor requestProcessor) {
		this.requestProcessorMap.put(requestProcessor.getPort(), requestProcessor);
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.telnet.TelnetCommandExecutor#getCmd()
	 */
	@Override
	public String getCmd() {
		return this.cmd;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.telnet.TelnetCommandExecutor#getCmdInfo()
	 */
	@Override
	public String getCmdInfo() {
		return this.desc;
	}
	
}
