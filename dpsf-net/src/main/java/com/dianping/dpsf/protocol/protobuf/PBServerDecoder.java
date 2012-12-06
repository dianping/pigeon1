/**
 * 
 */
package com.dianping.dpsf.protocol.protobuf;

import com.google.protobuf.MessageLite;

/**    
 * <p>    
 * Title: PBClientDecoder.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-19 下午05:25:41   
 */
public class PBServerDecoder extends PBDecoder{

	public PBServerDecoder() {
		super(DPSFProtos.Request.getDefaultInstance());
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.channel.protocol.Decoder#wrappedObject(com.google.protobuf.MessageLite)
	 */
	public Object wrappedObject(MessageLite message) {
		return new PBRequest(message);
	}

}
