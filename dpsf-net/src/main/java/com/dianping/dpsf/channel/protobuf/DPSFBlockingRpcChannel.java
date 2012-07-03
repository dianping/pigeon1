/**
 * 
 */
package com.dianping.dpsf.channel.protobuf;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.component.DPSFMetaData;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.impl.DefaultInvoker;
import com.dianping.dpsf.exception.NetException;
import com.dianping.dpsf.protocol.protobuf.DPSFProtos;
import com.dianping.dpsf.protocol.protobuf.PBRequest;
import com.google.protobuf.BlockingRpcChannel;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;

/**    
 * <p>    
 * Title: DPSFBlockingRpcChannel.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-17 上午12:26:54   
 */
public class DPSFBlockingRpcChannel implements BlockingRpcChannel{
	
	private static Logger log = DPSFLog.getLogger();
	
	private DPSFMetaData metaData;
	 
	public DPSFBlockingRpcChannel(DPSFMetaData metaData){
		this.metaData = metaData;
	}

	/* (non-Javadoc)
	 * @see com.google.protobuf.BlockingRpcChannel#callBlockingMethod(com.google.protobuf.Descriptors.MethodDescriptor, com.google.protobuf.RpcController, com.google.protobuf.Message, com.google.protobuf.Message)
	 */
	public Message callBlockingMethod(MethodDescriptor method, RpcController controller,
			Message request, Message responsePrototype)
			throws ServiceException {
		try {
			DPSFResponse response = DefaultInvoker.getInstance().invokeSync(
					new PBRequest(method,request, metaData.getServiceName(), Constants.MESSAGE_TYPE_SERVICE, metaData.getTimeout()), 
					metaData, new PBController(controller));
			DPSFProtos.Response res = (DPSFProtos.Response)response.getObject();
			if(res.getMessageType() == Constants.MESSAGE_TYPE_SERVICE){
				Message msg = responsePrototype.newBuilderForType().mergeFrom(res.getReturn()).build();
				return msg;
			}if(res.getMessageType() == Constants.MESSAGE_TYPE_EXCEPTION){
				log.error(res.getCause());
				throw new ServiceException(res.getCause());
			}else if(res.getMessageType() == Constants.MESSAGE_TYPE_SERVICE_EXCEPTION){
				throw new ServiceException(res.getCause());
			}
			
		} catch (NetException e) {
			ServiceException se = new ServiceException(e.getMessage());
			se.setStackTrace(e.getStackTrace());
			throw se;
		} catch (InvalidProtocolBufferException e) {
			ServiceException se = new ServiceException(e.getMessage());
			se.setStackTrace(e.getStackTrace());
			throw se;
		} catch (InterruptedException e) {
			ServiceException se = new ServiceException(e.getMessage());
			se.setStackTrace(e.getStackTrace());
			throw se;
		}
		return null;
	}

}
