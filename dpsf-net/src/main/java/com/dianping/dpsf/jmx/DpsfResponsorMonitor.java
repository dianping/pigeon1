/**
 * Project: ${dpsf-net.aid}
 * 
 * File Created at 2011-8-22
 * $Id$
 * 
 * Copyright 2010 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dianping.dpsf.jmx;

import java.lang.Thread.State;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.lang.StringUtils;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;

import com.dianping.dpsf.process.RequestProcessor;
import com.dianping.dpsf.repository.ServiceRepository;
import com.dianping.hawk.jmx.support.MBeanMeta;

/**
 * Monitor dpsf execution information as responsor
 * @author danson.liu
 *
 */
public class DpsfResponsorMonitor {
	
	private static DpsfResponsorMonitor instance = new DpsfResponsorMonitor();
	
	private Map<Integer, RequestProcessor> requestProcessors = new HashMap<Integer, RequestProcessor>();
	
	@MBeanMeta(ignore = true)
	public static DpsfResponsorMonitor getInstance() {
		return instance;
	}
	
	private DpsfResponsorMonitor() {
	}
	
	public String getHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return "unknown";
		}
	}
	
	public String getServicePorts() {
		StringBuilder builder = new StringBuilder();
		builder.append("<table border=\"1\" cellspacing=\"0\" cellspadding=\"0\">");
		builder.append("<thead><tr>");
		builder.append("<th style=\"text-align:left;\">Port</th>");
		builder.append("<th style=\"text-align:left;\">Name</th>");
		builder.append("<th style=\"text-align:left;\">Value</th>");
		builder.append("</tr></thead>");
		builder.append("<tbody>");
		for (Integer port : requestProcessors.keySet()) {
			builder.append("<tr>");
			builder.append("<td rowspan=\"11\">");
			builder.append(port);
			builder.append("</td>");
			builder.append(generateTD("Requestors"));
			builder.append(generateTD(StringUtils.join(requestorsAtPort(port), ", ")));
			builder.append("</tr>");
			builder.append(generateTR("ThreadPoolSize", getThreadPoolSize(port)));
			builder.append(generateTR("WaitTaskInQueue", getWaitTaskInQueue(port)));
			builder.append(generateTR("ActiveThreadCount", getActiveThreadCount(port)));
			builder.append(generateTR("BlockedThreadCount", getBlockedThreadCount(port)));
			builder.append(generateTR("NewThreadCount", getNewThreadCount(port)));
			builder.append(generateTR("RunnableThreadCount", getRunnableThreadCount(port)));
			builder.append(generateTR("TerminatedThreadCount", getTerminatedThreadCount(port)));
			builder.append(generateTR("TimedWaitingThreadCount", getTimedWaitingThreadCount(port)));
			builder.append(generateTR("WaitingThreadCount", getWaitingThreadCount(port)));
			builder.append(generateTR("servicesAtPort", StringUtils.join(servicesAtPort(port), "<br />")));
		}
		builder.append("</tbody>");
		builder.append("</table>");
		return builder.toString();
	}
	
	private String generateTR(Object... contents) {
		StringBuilder builder = new StringBuilder();
		builder.append("<tr>");
		for (Object content : contents) {
			builder.append("<td>").append(content).append("</td>");
		}
		builder.append("</tr>");
		return builder.toString();
	}

	private String generateTD(String content) {
		return "<td>" + content + "</td>";
	}

	/**
	 * get services at specified port
	 * @param port
	 * @return
	 */
	@MBeanMeta(ignore = true)
	public Collection<String> servicesAtPort(int port) {
		RequestProcessor requestProcessor = getRequestProcessor(port);
		if (requestProcessor == null) {
			return Collections.emptySet();
		}
		ServiceRepository serviceRepository = requestProcessor.getServiceRepository();
		return serviceRepository.getServiceNames();
	}
	
	@MBeanMeta(ignore = true)
	public Collection<String> requestorsAtPort(int port) {
		Collection<String> requestors = new HashSet<String>();
		RequestProcessor requestProcessor = getRequestProcessor(port);
		if (requestProcessor == null) {
			return Collections.emptySet();
		}
		ChannelGroup serverChannels = requestProcessor.getServerChannels();
		for (Channel channel : serverChannels) {
			InetSocketAddress remoteAddress = (InetSocketAddress) channel.getRemoteAddress();
			requestors.add(remoteAddress.getAddress().getHostAddress() + ":" + remoteAddress.getPort());
		}
		return requestors;
	}
	
	//thread info
	@MBeanMeta(ignore = true)
	public int getThreadPoolSize(int port) {
		ThreadPoolExecutor threadPool = getThreadPool(port);
		return threadPool != null ? threadPool.getPoolSize() : -1;
	}
	
	@MBeanMeta(ignore = true)
	public int getActiveThreadCount(int port) {
		ThreadPoolExecutor threadPool = getThreadPool(port);
		return threadPool != null ? threadPool.getActiveCount() : -1;
	}
	
	@MBeanMeta(ignore = true)
	public int getWaitTaskInQueue(int port) {
		ThreadPoolExecutor threadPool = getThreadPool(port);
		return threadPool != null ? threadPool.getQueue().size() : -1;
	}
	
	@MBeanMeta(ignore = true)
	public int getNewThreadCount(int port) {
		RequestProcessor requestProcessor = getRequestProcessor(port);
		return requestProcessor != null ? getThreadCount(requestProcessor, State.NEW) : -1;
	}
	
	@MBeanMeta(ignore = true)
	public int getRunnableThreadCount(int port) {
		RequestProcessor requestProcessor = getRequestProcessor(port);
		return requestProcessor != null ? getThreadCount(requestProcessor, State.RUNNABLE) : -1;
	}
	
	@MBeanMeta(ignore = true)
	public int getBlockedThreadCount(int port) {
		RequestProcessor requestProcessor = getRequestProcessor(port);
		return requestProcessor != null ? getThreadCount(requestProcessor, State.BLOCKED) : -1;
	}
	
	@MBeanMeta(ignore = true)
	public int getWaitingThreadCount(int port) {
		RequestProcessor requestProcessor = getRequestProcessor(port);
		return requestProcessor != null ? getThreadCount(requestProcessor, State.WAITING) : -1;
	}
	
	@MBeanMeta(ignore = true)
	public int getTimedWaitingThreadCount(int port) {
		RequestProcessor requestProcessor = getRequestProcessor(port);
		return requestProcessor != null ? getThreadCount(requestProcessor, State.TIMED_WAITING) : -1;
	}
	
	@MBeanMeta(ignore = true)
	public int getTerminatedThreadCount(int port) {
		RequestProcessor requestProcessor = getRequestProcessor(port);
		return requestProcessor != null ? getThreadCount(requestProcessor, State.TERMINATED) : -1;
	}
	
	@MBeanMeta(order = -1)
	public String getNewThreadStackTrace(int port, int threadCount) {
		RequestProcessor requestProcessor = getRequestProcessor(port);
		if (requestProcessor == null) {
			return null;
		}
		return getThreadStackTraces(requestProcessor, State.NEW, threadCount);
	}
	
	@MBeanMeta(order = -1)
	public String getRunnableThreadStackTrace(int port, int threadCount) {
		RequestProcessor requestProcessor = getRequestProcessor(port);
		if (requestProcessor == null) {
			return null;
		}
		return getThreadStackTraces(requestProcessor, State.RUNNABLE, threadCount);
	}
	
	@MBeanMeta(order = -1)
	public String getBlockedThreadStackTrace(int port, int threadCount) {
		RequestProcessor requestProcessor = getRequestProcessor(port);
		if (requestProcessor == null) {
			return null;
		}
		return getThreadStackTraces(requestProcessor, State.BLOCKED, threadCount);
	}
	
	@MBeanMeta(order = -1)
	public String getWaitingThreadStackTrace(int port, int threadCount) {
		RequestProcessor requestProcessor = getRequestProcessor(port);
		if (requestProcessor == null) {
			return null;
		}
		return getThreadStackTraces(requestProcessor, State.WAITING, threadCount);
	}
	
	@MBeanMeta(order = -1)
	public String getTimedWaitingThreadStackTrace(int port, int threadCount) {
		RequestProcessor requestProcessor = getRequestProcessor(port);
		if (requestProcessor == null) {
			return null;
		}
		return getThreadStackTraces(requestProcessor, State.TIMED_WAITING, threadCount);
	}
	
	@MBeanMeta(order = -1)
	public String getTerminatedThreadStackTrace(int port, int threadCount) {
		RequestProcessor requestProcessor = getRequestProcessor(port);
		if (requestProcessor == null) {
			return null;
		}
		return getThreadStackTraces(requestProcessor, State.TERMINATED, threadCount);
	}
	
	private String getThreadStackTraces(RequestProcessor requestProcessor, State state, int threadCount) {
		ThreadGroup threadGroup = requestProcessor.getThreadPool().getFactory().getGroup();
		Thread[] threads = new Thread[threadGroup.activeCount()];
		threadGroup.enumerate(threads,false);
		StringBuilder builder = new StringBuilder();
		int count = 0;
		if (threads != null && threads.length > 0 && threadCount > 0) {
			for (Thread thread : threads) {
				if (state == thread.getState()) {
					count++;
					if (count > 1) {builder.append("\r\n\r\n");}
					builder.append("Thread ").append(thread.getId()).append("  ").append(thread.getName())
						.append(" (state = ").append(state).append(")").append("\r\n");
					StackTraceElement[] stackTrace = thread.getStackTrace();
					for (StackTraceElement ste : stackTrace) {
						builder.append(ste.getClassName()).append("-").append(ste.getMethodName())
							.append("(").append(ste.getLineNumber()).append(")").append("\r\n");
					}
					if (count >= threadCount) {
						break;
					}
				}
			}
		}
		return builder.toString();
	}

	private int getThreadCount(RequestProcessor requestProcessor, State state) {
		ThreadGroup threadGroup = requestProcessor.getThreadPool().getFactory().getGroup();
		Thread[] threads = new Thread[threadGroup.activeCount()];
		threadGroup.enumerate(threads, false);
		int threadCount = 0;
		for(Thread t : threads) {
			if (state == t.getState()) {
				threadCount++;
			}
		}
		return threadCount;
	}
	
	private ThreadPoolExecutor getThreadPool(int port) {
		RequestProcessor requestProcessor = getRequestProcessor(port);
		if (requestProcessor == null) {
			return null;
		}
		return requestProcessor.getThreadPool().getExecutor();
	}
	
	@MBeanMeta(ignore = true)
	public void addRequestProcessor(RequestProcessor requestProcessor) {
		this.requestProcessors.put(requestProcessor.getPort(), requestProcessor);
	}

	private RequestProcessor getRequestProcessor(int port) {
		return requestProcessors.get(port);
	}

}
