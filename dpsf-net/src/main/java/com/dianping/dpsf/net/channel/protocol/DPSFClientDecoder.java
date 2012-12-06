/**
 * 
 */
package com.dianping.dpsf.net.channel.protocol;

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.Channels;

import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.exception.NetException;
import com.dianping.dpsf.process.ResponseFactory;

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
public class DPSFClientDecoder extends DPSFDecoder{

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.channel.protocol.DPSFDecoder#getDecoder(int)
	 */
	@Override
	public Decoder getDecoder(int serializable) throws NetException {

		return EncoderAndDecoderFactory.getClientDecoder(serializable);
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.net.channel.protocol.DPSFDecoder#doInitMsg(java.lang.Object)
	 */
	@Override
	public Object doInitMsg(Object message) {
		
		return message;
	}

	@Override
	public void doFailResponse(Channel channel, DPSFResponse response) {
		List<DPSFResponse> respList = new ArrayList<DPSFResponse>();
		respList.add(response);
		Channels.fireMessageReceived(channel,respList);
	}

}
