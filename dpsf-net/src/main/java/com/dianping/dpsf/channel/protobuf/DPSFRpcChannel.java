/**
 * 
 */
package com.dianping.dpsf.channel.protobuf;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.component.DPSFMetaData;
import com.dianping.dpsf.component.impl.DefaultInvoker;
import com.dianping.dpsf.exception.NetException;
import com.dianping.dpsf.protocol.protobuf.PBRequest;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcChannel;
import com.google.protobuf.RpcController;

/**    
 * <p>    
 * Title: DPSFRpcChannel.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-17 上午12:26:22   
 */
public class DPSFRpcChannel implements RpcChannel{
	
	private DPSFMetaData metaData;
	
	private RpcController defaultController;
	
	public DPSFRpcChannel(DPSFMetaData metaData){
		this.metaData = metaData;
		this.defaultController = new DefaultRpcController();
	}

	/* (non-Javadoc)
	 * @see com.google.protobuf.RpcChannel#callMethod(com.google.protobuf.Descriptors.MethodDescriptor, com.google.protobuf.RpcController, com.google.protobuf.Message, com.google.protobuf.Message, com.google.protobuf.RpcCallback)
	 */
	public void callMethod(MethodDescriptor method, RpcController controller,
			Message request, Message responsePrototype, RpcCallback<Message> done) {
		try {
			DefaultInvoker.getInstance().invokeCallback(new PBRequest(method,request, metaData.getServiceName(), Constants.MESSAGE_TYPE_SERVICE, metaData.getTimeout()),
					metaData, new PBController(controller), new PBCallback(responsePrototype,done));
		} catch (NetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
