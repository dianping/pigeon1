/**
 * 
 */
package com.dianping.dpsf.net.channel.protocol;

import org.jboss.netty.buffer.ChannelBuffer;

/**    
 * <p>    
 * Title: AbstractEncoder.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-10-13 上午10:43:08   
 */
public abstract class AbstractEncoder implements Encoder{
	
	protected static final byte[] LENGTH_PLACEHOLDER = new byte[7];
	
	protected void afterDo(ChannelBuffer cb){
		cb.setInt(3, cb.writerIndex() - 7);
	}

}
