/**
 * 
 */
package com.dianping.dpsf.component;

import com.dianping.dpsf.net.channel.Client;

/**    
 * <p>    
 * Title: DPSFCall.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-12-9 下午06:22:30   
 */
public interface DPSFCall {

	public void setClient(Client client);
	
	public Client getClient();
}
