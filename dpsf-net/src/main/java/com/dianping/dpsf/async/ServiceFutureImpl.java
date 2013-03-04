/**
 * 
 */
package com.dianping.dpsf.async;



import java.util.concurrent.TimeUnit;

import com.dianping.cat.Cat;
import com.dianping.dpsf.Constants;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.impl.CallbackFuture;
import com.dianping.dpsf.exception.DPSFException;

/**    
 * <p>    
 * Title: ServiceFutureImpl.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2011-3-22 上午09:53:06   
 */
public class ServiceFutureImpl extends CallbackFuture implements ServiceFuture{
	
	private long timeout = Long.MAX_VALUE;
	
	public ServiceFutureImpl(long timeout){
		super();
		this.timeout = timeout;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.async.ServiceFuture#_get()
	 */
	@Override
	public Object _get() throws InterruptedException, DPSFException {
		return _get(this.timeout);
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.async.ServiceFuture#_get(long)
	 */
	@Override
	public Object _get(long timeoutMillis) throws InterruptedException,
	DPSFException {
		
		try {
			DPSFResponse res = super.get(timeoutMillis);
			if(res.getMessageType() == Constants.MESSAGE_TYPE_SERVICE){
				return res.getReturn();
			}else if(res.getMessageType() == Constants.MESSAGE_TYPE_EXCEPTION){
				logger.error(res.getCause());
				DPSFException dpsfE = new DPSFException(res.getCause());
				Cat.getProducer().logError(dpsfE);
				throw dpsfE;
			}else if(res.getMessageType() == Constants.MESSAGE_TYPE_SERVICE_EXCEPTION){
				DPSFException dpsfE = new DPSFException((Throwable)res.getReturn());
				Cat.getProducer().logError(dpsfE);
				throw dpsfE;
			}else{
				throw new DPSFException("error messageType:"+res.getMessageType());
			}
			
		} catch (Exception e) {
			DPSFException dpsfE = new DPSFException(e);
			Cat.getProducer().logError(dpsfE);
			throw dpsfE;
		}
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.async.ServiceFuture#_get(long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public Object _get(long timeout, TimeUnit unit)
			throws InterruptedException, DPSFException {
		return _get(unit.toMillis(timeout));
	}
	
}
