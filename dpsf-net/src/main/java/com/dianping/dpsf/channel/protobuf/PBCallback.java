/**
 * 
 */
package com.dianping.dpsf.channel.protobuf;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelFuture;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.RequestError;
import com.dianping.dpsf.component.DPSFCallback;
import com.dianping.dpsf.component.DPSFFuture;
import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.impl.DefaultDPSFFuture;
import com.dianping.dpsf.exception.NetException;
import com.dianping.dpsf.net.channel.Client;
import com.dianping.dpsf.protocol.protobuf.DPSFProtos;
import com.dianping.dpsf.stat.CentralStatService.CentralStatContext;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.RpcCallback;

/**    
 * <p>    
 * Title: PBCallback.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-19 上午12:19:36   
 */
public class PBCallback implements DPSFCallback{
	
	private static final Logger log = DPSFLog.getLogger();
	
	private RpcCallback<Message> callback;
	
	private Message responsePrototype;
	
	private DPSFResponse response;
	
	private DPSFFuture future = new DefaultDPSFFuture();
	
	private DPSFRequest request;

	private Client client;
	
	public PBCallback(Message responsePrototype,RpcCallback<Message> callback){
		this.callback = callback;
		this.responsePrototype = responsePrototype;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFCallback#callback()
	 */
	public void callback(DPSFResponse response) {
		this.response = response;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		DPSFProtos.Response protobufRes = null;
		try {
			protobufRes = (DPSFProtos.Response)response.getObject();
		} catch (NetException e1) {
			log.error(e1.getMessage(),e1);
			return;
		}
		if(protobufRes.getMessageType() == Constants.MESSAGE_TYPE_EXCEPTION){
			log.error(protobufRes.getCause());
		}
		try {
			callback.run(this.responsePrototype.newBuilderForType().mergeFrom(protobufRes.getReturn()).build());
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFCallback#getFuture(org.jboss.netty.channel.ChannelFuture)
	 */
	public DPSFFuture getFuture(ChannelFuture future) {
		return this.future;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFCallback#fail(java.lang.Error)
	 */
	public void fail(RequestError error) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFCallback#setRequest(com.dianping.dpsf.component.DPSFRequest)
	 */
	public void setRequest(DPSFRequest request) {
		this.request = request;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFCall#setClient(com.dianping.dpsf.net.channel.Client)
	 */
	@Override
	public void setClient(Client client) {
		this.client = client;
	}

	@Override
	public Client getClient() {
		return this.client;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFCallback#getRequest()
	 */
	@Override
	public DPSFRequest getRequest() {
		// TODO Auto-generated method stub
		return this.request;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFCallback#setCentralStatContext(com.dianping.dpsf.stat.CentralStatService.CentralStatContext)
	 */
	@Override
	public void setCentralStatContext(CentralStatContext centralStatContext) {
		// TODO Auto-generated method stub
		
	}

}
