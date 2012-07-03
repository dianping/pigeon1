/**
 * 
 */
package com.dianping.dpsf.component;

import java.io.Serializable;

import com.dianping.dpsf.exception.NetException;

/**    
 * <p>    
 * Title: DPSFSerializable.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-19 下午03:23:51   
 */
public interface DPSFSerializable extends Serializable{
	
	public byte getSerializ();
	
	public void setSequence(long seq);
	
	public long getSequence();
	
	public Object getObject()throws NetException;
	
	public Object getContext();
	
	public void setContext(Object context);
	
}
