/**
 * 
 */
package com.dianping.dpsf.protocol.thrift;

import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.Version;
import com.dianping.dpsf.channel.thrift.ChannelBufferTTransport;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.exception.DPSFRuntimeException;
import com.dianping.dpsf.exception.NetException;
import com.dianping.dpsf.exception.ServiceException;

/**    
 * <p>    
 * Title: ThriftResponse.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-9-14 下午02:48:40   
 */
public class ThriftResponse implements DPSFResponse{
	
	private Response response;
	
	private TBase returnVal;
	
	private long seq;
	
	private int messageType;
	
	private String cause;
	
	private ChannelBufferTTransport transport;
	private TProtocol protocol;
	
	public ThriftResponse(long seq,int messageType,TBase returnVal){
		this.seq = seq;
		this.messageType = messageType;
		this.returnVal = returnVal;
		
	}
	
	public ThriftResponse(long seq,int messageType,String cause){
		this.seq = seq;
		this.messageType = messageType;
		this.cause = cause;
		
	}
	
	public ThriftResponse(Response response){
		this.response = response;
	}
	

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFSerializable#getSerializ()
	 */
	public byte getSerializ() {
		return Constants.SERILIZABLE_THRIFT;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFSerializable#setSequence(long)
	 */
	public void setSequence(long seq) {
		this.seq = seq;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFSerializable#getSequence()
	 */
	public long getSequence() {
		if(this.seq > 0){
			return this.seq;
		}
		if(this.response != null){
			return this.response.getTransferId();
		}
		throw new DPSFRuntimeException("seq is null");
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFSerializable#getObject()
	 */
	public Object getObject() throws NetException {

		if(this.response == null){
			try {
				this.response = createResponse();
			} catch (TException e) {
				throw new NetException(e.getMessage(),e);
			}
		}
		return this.response;
	}
	
	private Response createResponse() throws TException{
		
		this.response = new Response(this.messageType,this.seq,Version.getCurrentVersion(),null,null);
		if(this.returnVal != null){
			this.transport = new ChannelBufferTTransport();
			this.protocol = new TBinaryProtocol(transport);
			this.returnVal.write(this.protocol);
			this.response.setReturnVal(this.transport.getChannelBuffer().toByteBuffer());
		}
		if(this.cause != null){
			this.response.setCause(this.cause);
		}
		return this.response;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFResponse#setMessageType(int)
	 */
	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFResponse#getMessageType()
	 */
	public int getMessageType() throws ServiceException {

		if(this.messageType > 0){
			return this.messageType;
		}
		if(this.response != null){
			return this.response.getMessageType();
		}
		throw new DPSFRuntimeException("messageType is null");
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFResponse#getCause()
	 */
	public String getCause() {

		if(this.cause != null){
			return this.cause;
		}
		if(this.response != null){
			return this.response.getCause();
		}
		throw new DPSFRuntimeException("cause is null");
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFResponse#getReturn()
	 */
	public Object getReturn() {

		if(this.response != null){
			return this.response.getReturnVal();
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
