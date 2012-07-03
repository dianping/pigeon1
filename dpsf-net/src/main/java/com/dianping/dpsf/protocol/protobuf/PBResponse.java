/**
 * 
 */
package com.dianping.dpsf.protocol.protobuf;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.Version;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.exception.ServiceException;
import com.google.protobuf.MessageLite;

/**    
 * <p>    
 * Title: PBResponse.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-19 下午02:07:43   
 */
public class PBResponse implements DPSFResponse{
	
	private DPSFProtos.Response response;
	
	private long seq;
	
	private MessageLite returnRes;
	
	private int messageType;
	
	private String cause;
	
	public PBResponse(MessageLite response){
		if(response instanceof DPSFProtos.Response){
			this.response = (DPSFProtos.Response)response;
		}else{
			this.returnRes = response;
		}
		
	}
	
	public PBResponse(String cause){
		this.cause = cause;
	}
	public PBResponse(){
	}


	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFSerializable#getObject()
	 */
	public Object getObject() {
		if(this.response == null){
			this.response = createResponse();
		}
		return this.response;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFSerializable#setSequence(long)
	 */
	public void setSequence(long seq) {
		this.seq = seq;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFSerializable#getSequence()
	 */
	public long getSequence() {
		if(this.seq > 0){
			return this.seq;
		}else{
			return this.response.getTransferId();
		}
	}
	
	/**
	 * @param messageType the messageType to set
	 */
	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}


	private DPSFProtos.Response createResponse(){
		DPSFProtos.Response res = null;
		DPSFProtos.Response.Builder builder = DPSFProtos.Response.newBuilder()
		.setMessageType(this.messageType).setTransferId(this.seq)
		.setRpcVersion(Version.getCurrentVersion());
		if(this.returnRes != null){
			builder.setReturn(this.returnRes.toByteString());
		}else if(this.cause != null){
			builder.setCause(this.cause);
		}
		res = builder.build();
		
		return res;
	}


	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFSerializable#getSerializ()
	 */
	public byte getSerializ() {
		return Constants.SERILIZABLE_PB;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFResponse#getMessageType()
	 */
	public int getMessageType() throws ServiceException {
		if(this.messageType > 0){
			return this.messageType;
		}
		if(this.response == null){
			throw new ServiceException("response is null");
		}
		return this.response.getMessageType();
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFResponse#getCause()
	 */
	public String getCause() {
		if(this.response != null){
			return this.response.getCause();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFResponse#getReturn()
	 */
	public Object getReturn() {
		if(this.response != null){
			if (this.response.getMessageType() == Constants.MESSAGE_TYPE_SERVICE_EXCEPTION) {
				return new com.google.protobuf.ServiceException(this.response.getCause());
			} else {
				return this.response.getReturn();
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFSerializable#getContext()
	 */
	@Override
	public Object getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFSerializable#setContext(java.lang.Object)
	 */
	@Override
	public void setContext(Object context) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFResponse#setReturn(java.lang.Object)
	 */
	@Override
	public void setReturn(Object obj) {
		// TODO Auto-generated method stub
		
	}

}
