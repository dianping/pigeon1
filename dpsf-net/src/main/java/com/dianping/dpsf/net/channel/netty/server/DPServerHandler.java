package com.dianping.dpsf.net.channel.netty.server;

import java.net.InetAddress;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.dianping.cat.Cat;
import com.dianping.dpsf.Constants;
import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.net.channel.Server;
import com.dianping.dpsf.net.channel.netty.NettyChannel;
import com.dianping.dpsf.process.RequestProcessor;
import com.dianping.dpsf.process.ResponseFactory;
import com.dianping.dpsf.protocol.protobuf.DPSFProtos;

/**    
  * <p>    
  * Title: DPServerHandler.java   
  * </p>    
  * <p>    
  * Description: 描述  
  * </p>   
  * @author saber miao   
  * @version 1.0    
  * @created 2010-8-3 上午10:47:28   
  */ 
public class DPServerHandler extends SimpleChannelUpstreamHandler{
	
	private static Logger log = DPSFLog.getLogger();
	
	 private RequestProcessor processor = null;
	 
	 public DPServerHandler(RequestProcessor processor){
		 this.processor = processor;
	 }
	 
	 @Override
	 public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
		   if (e instanceof ChannelStateEvent && ((ChannelStateEvent) e).getState() != ChannelState.INTEREST_OPS) {
		       log.info(e.toString());
		   }
		   super.handleUpstream(ctx, e);
	 }
	 
	 @SuppressWarnings("unchecked")
	@Override
	 public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		 List<DPSFRequest> messages = (List<DPSFRequest>) (e.getMessage());
		 for(DPSFRequest request : messages){
			 try{
				 this.processor.addRequest(request,ctx.getChannel()); 
			 }catch(Exception e1) {
				 String msg = "Request execute fail:seq--"+request.getSequence()+"\r\n";
				 //心跳消息只返回正常的, 异常不返回
				 if(request.getCallType() == Constants.CALLTYPE_REPLY && request.getMessageType() != Constants.MESSAGE_TYPE_HEART) {
					 ctx.getChannel().write(ResponseFactory.createFailResponse(request,msg));
				 }
				 log.error(msg+"****SEQ:"+request.getSequence()+"****callType:"+request.getCallType(),e1);
				 Cat.getProducer().logError(e1);
			 }
			 
		 }
		
	  }
	 
	 @Override
	 public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
	 }
	 
	 @Override 

	 public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) {  
		 this.processor.addChannel(new NettyChannel(e.getChannel()));
	 }
	 
	 @Override
	 public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
	     log.error(e.getCause().getMessage(),e.getCause());
	     e.getChannel().close();
	 }

}
