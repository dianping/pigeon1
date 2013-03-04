/**
 * 
 */
package com.dianping.dpsf.telnet;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.thread.ExeThreadPool;

/**    
 * <p>    
 * Title: TelnetCommond.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2011-2-25 上午11:50:02   
 */
public class TelnetDPSFCommand extends TelnetCommand{
	
	
	public TelnetDPSFCommand(){
		super(new ExeThreadPool(Constants.THREADNAME_TELNET_SYSTEM_PROCESSOR,1,10));
	}
	
	public void registerState(TelnetCommandExecutor executor){
		this.cmds.put(executor.getCmd(), new TelnetCommandInfo(executor.getCmd(),executor.getCmdInfo(),executor));
	}
	

}
