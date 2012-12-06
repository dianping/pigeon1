/**
 * 
 */
package com.dianping.dpsf.telnet;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.netty.channel.Channel;

import com.dianping.dpsf.exception.DPSFTelnetException;
import com.dianping.dpsf.thread.DPSFThreadPool;

/**    
 * <p>    
 * Title: TelnetCommond.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2011-2-28 上午11:05:45   
 */
public class TelnetCommand {
	
	private DPSFThreadPool threadPool;
	
	protected Map<String,TelnetCommandInfo> cmds;
	
	public TelnetCommand(DPSFThreadPool threadPool){
		this.threadPool = threadPool;
		this.cmds = new HashMap<String,TelnetCommandInfo>();
	}
	
	boolean execute(Channel channel,String[] cmds){
		if(cmds == null || cmds.length== 0){
			return false;
		}
		TelnetCommandInfo tcmdi = this.cmds.get(cmds[0]);
		if(tcmdi == null){
			return false;
		}
		this.threadPool.execute(new TelnetExecutor(channel,tcmdi,cmds));
		return true;
	}
	
	String getCMDInfo(){
		
		StringBuffer sb = new StringBuffer();
		for(Entry<String,TelnetCommandInfo> tcmi : this.cmds.entrySet()){
			sb.append("** ");
			sb.append(tcmi.getKey());
			sb.append("\r\n");
			sb.append("**      ");
			sb.append(tcmi.getValue().getDescription());
			sb.append("\r\n");
		}
		sb.append("\r\n");
		return sb.toString();
	}
	
	class TelnetExecutor implements Runnable{
		
		private Channel channel;
		private TelnetCommandInfo tcmdi;
		private String[] cmds;
		
		public TelnetExecutor(Channel channel,TelnetCommandInfo tcmdi,String[] cmds){
			this.channel = channel;
			this.tcmdi = tcmdi;
			this.cmds = cmds;
		}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try {
				String result = this.tcmdi.getExecutor().execute(this.cmds);
				if(result != null){
					this.channel.write(result);
				}
			} catch (DPSFTelnetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.channel.write("\r\n>>");
		}
		
	}

}
