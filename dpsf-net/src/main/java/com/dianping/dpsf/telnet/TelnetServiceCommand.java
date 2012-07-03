/**
 * 
 */
package com.dianping.dpsf.telnet;


import com.dianping.dpsf.thread.DPSFThreadPool;
import com.dianping.dpsf.thread.ExeThreadPool;

/**    
 * <p>    
 * Title: TelnetServiceCommond.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2011-2-25 上午11:52:07   
 */
public class TelnetServiceCommand extends TelnetCommand{
	
	private DPSFThreadPool threadPool;
	
	public TelnetServiceCommand(){
		super(new ExeThreadPool("Server-TelnetServiceExecutor",1,10));
	}
	
	public Object register(String cmd,TelnetCommandInfo info){
		return this.cmds.put(cmd, info);
	}

}
