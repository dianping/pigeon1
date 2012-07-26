/**
 * 
 */
package com.dianping.dpsf.channel.protobuf;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

/**    
 * <p>    
 * Title: DPSFRpcController.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-17 上午09:53:02   
 */
public class DefaultRpcController implements RpcController{

	/* (non-Javadoc)
	 * @see com.google.protobuf.RpcController#errorText()
	 */
	public String errorText() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.google.protobuf.RpcController#failed()
	 */
	public boolean failed() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.google.protobuf.RpcController#isCanceled()
	 */
	public boolean isCanceled() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.google.protobuf.RpcController#notifyOnCancel(com.google.protobuf.RpcCallback)
	 */
	public void notifyOnCancel(RpcCallback<Object> arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.google.protobuf.RpcController#reset()
	 */
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.google.protobuf.RpcController#setFailed(java.lang.String)
	 */
	public void setFailed(String arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.google.protobuf.RpcController#startCancel()
	 */
	public void startCancel() {
		// TODO Auto-generated method stub
		
	}

}
