/**
 * 
 */
package com.dianping.dpsf.net.channel.protocol;

import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.exception.NetException;

/**    
 * <p>    
 * Title: DPSFClientDecoder.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-19 下午11:23:55   
 */
public class DPSFServerDecoder extends DPSFDecoder{

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.channel.protocol.DPSFDecoder#getDecoder(int)
	 */
	@Override
	public Decoder getDecoder(int serializable) throws NetException {

		return EncoderAndDecoderFactory.getServerDecoder(serializable);
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.channel.protocol.DPSFDecoder#doInitMsg(java.lang.Object)
	 */
	@Override
	public Object doInitMsg(Object message) {
		if(message == null){
			return null;
		}
		DPSFRequest request = (DPSFRequest)message;
		request.createMillisTime();
		return request;
	}

}
