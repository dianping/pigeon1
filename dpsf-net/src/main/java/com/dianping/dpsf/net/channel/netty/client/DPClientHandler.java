package com.dianping.dpsf.net.channel.netty.client;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.DPSFUtils;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.net.channel.Client;
import com.dianping.dpsf.stat.RpcStatsPool;
import com.dianping.dpsf.thread.DPSFThreadPool;

/**    
  * <p>    
  * Title: DPClientHandler.java   
  * </p>    
  * <p>    
  * Description: 描述  
  * </p>   
  * @author saber miao   
  * @version 1.0    
  * @created 2010-8-3 上午10:47:14   
  */ 
public class DPClientHandler extends SimpleChannelUpstreamHandler{
	
	private static Logger log = DPSFLog.getLogger();
	
	 private Client client;
	 
	 private DPSFThreadPool threadPool;
	 
	 public DPClientHandler(Client client,DPSFThreadPool threadPool){
		 this.client = client;
		 this.threadPool = threadPool;
	 }
	 
	 @Override
	 public void handleUpstream(
		    ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
		   super.handleUpstream(ctx, e);
		   
		}
		 
	 @Override
	 public void messageReceived(
	          ChannelHandlerContext ctx, MessageEvent e) {
		 List<DPSFResponse> messages = (List<DPSFResponse>)e.getMessage();
		 for(final DPSFResponse response : messages){
			 Runnable task = new Runnable(){
				 public void run() {
					 client.doResponse(response);
				 }
			 };
			 try{
				//TODO [v1.7.0, danson.liu]对于callback调用, 防止callback阻塞response handler thread pool线程池, 影响其他正常响应无法处理
				 this.threadPool.execute(task); 
			 }catch(Exception ex){
				 String msg = "Response execute fail:seq--"+response.getSequence()+"\r\n";
				 log.error(msg+ex.getMessage(),ex);
			 }
			 
		 }
		 
	 }
	 
	 @Override
	 public void channelConnected(
	         ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
	 }
	 
	 @Override
	 public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		 final ExceptionEvent e_ = e;
		 final Object attachment = DPSFUtils.getAttachment(ctx,Constants.ATTACHMENT_RETRY);
		 flowOutRequest(attachment);
		 if(e.getCause() instanceof IOException){
			 e.getChannel().close(); 
			 Runnable task = new Runnable(){
				public void run() {
					client.connectionException(attachment,e_);
				}
			 };
		     this.threadPool.execute(task);
		 }
	 }

	private void flowOutRequest(final Object attachment) {
		DPSFRequest request = getRequest(attachment);
		 if (request != null) {
			RpcStatsPool.flowOut(request, client.getAddress());
		 }
	}

	private DPSFRequest getRequest(Object attachment) {
		if (attachment instanceof Object[]) {
			Object[] msg = (Object[]) attachment;
			for (Object ele : msg) {
				if (ele instanceof DPSFRequest) {
					return (DPSFRequest) ele;
				}
			}
		} else if (attachment instanceof DPSFRequest) {
			return (DPSFRequest) attachment;
		}
		return null;
	}

}
