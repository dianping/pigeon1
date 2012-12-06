/**
 * 
 */
package com.dianping.dpsf.telnet;

/**    
 * <p>    
 * Title: TelnetCommondInfo.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2011-2-28 上午11:12:17   
 */
public class TelnetCommandInfo {
	
	private String cmd;
	private String description;
	private TelnetCommandExecutor executor;
	
	public TelnetCommandInfo(String cmd,String description,TelnetCommandExecutor executor){
		this.cmd = cmd;
		this.description = description;
		this.executor = executor;
	}

	/**
	 * @return the cmd
	 */
	public String getCmd() {
		return cmd;
	}

	/**
	 * @param cmd the cmd to set
	 */
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the executor
	 */
	public TelnetCommandExecutor getExecutor() {
		return executor;
	}

	/**
	 * @param executor the executor to set
	 */
	public void setExecutor(TelnetCommandExecutor executor) {
		this.executor = executor;
	}

}
