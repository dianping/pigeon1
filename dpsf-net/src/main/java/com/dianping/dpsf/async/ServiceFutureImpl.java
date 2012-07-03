/**
 * 
 */
package com.dianping.dpsf.async;



import java.util.concurrent.TimeUnit;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.dpsf.component.impl.CallbackFuture;
import com.dianping.dpsf.exception.DPSFException;
import com.dianping.dpsf.exception.ServiceException;

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
		DPSFResponse res = super.get(timeoutMillis);
		try {
			if(res.getMessageType() == Constants.MESSAGE_TYPE_SERVICE){
				return res.getReturn();
			}else if(res.getMessageType() == Constants.MESSAGE_TYPE_EXCEPTION){
				logger.error(res.getCause());
				throw new DPSFException(res.getCause());
			}else if(res.getMessageType() == Constants.MESSAGE_TYPE_SERVICE_EXCEPTION){
				throw new DPSFException((Throwable)res.getReturn());
			}else{
				throw new DPSFException("error messageType:"+res.getMessageType());
			}
			
		} catch (ServiceException e) {
			throw new DPSFException(e);
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
