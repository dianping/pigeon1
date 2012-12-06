/**
 * 
 */
package com.dianping.dpsf.protocol;

import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.exception.ServiceException;

/**    
 * <p>    
 * Title: DefaultResponse.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-9-7 下午10:14:30   
 */
public class DefaultResponse implements DPSFResponse{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4200559704846455821L;

	private transient byte serialize;
	
	private long seq;
	
	private int messageType;
	
	private String cause;
	
	private Object returnVal;
	
	private Object context;
	
	public DefaultResponse(int messageType,byte serialize){
		this.messageType = messageType;
		this.serialize = serialize;
	}
	
	public DefaultResponse(byte serialize,long seq,int messageType,Object returnVal){
		this.serialize = serialize;
		this.seq = seq;
		this.messageType = messageType;
		this.returnVal = returnVal;
	}
	
	public DefaultResponse(byte serialize,long seq,int messageType,String cause){
		this.serialize = serialize;
		this.seq = seq;
		this.messageType = messageType;
		this.cause = cause;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFSerializable#getSerializ()
	 */
	public byte getSerializ() {
		return this.serialize;
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
		return this.seq;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFSerializable#getObject()
	 */
	public Object getObject() {
		return this;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFResponse#setMessageType(int)
	 */
	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.component.DPSFResponse#getMessageType()
	 */
	public int getMessageType() throws ServiceException {
		return this.messageType;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFResponse#getCause()
	 */
	public String getCause() {
		// TODO Auto-generated method stub
		return this.cause;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFResponse#getReturn()
	 */
	public Object getReturn() {
		return this.returnVal;
	}
	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFSerializable#getContext()
	 */
	@Override
	public Object getContext() {
		return this.context;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFSerializable#setContext(java.lang.Object)
	 */
	@Override
	public void setContext(Object context) {
		this.context = context;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.component.DPSFResponse#setReturn(java.lang.Object)
	 */
	@Override
	public void setReturn(Object obj) {
		this.returnVal = obj;
	}
	
}
