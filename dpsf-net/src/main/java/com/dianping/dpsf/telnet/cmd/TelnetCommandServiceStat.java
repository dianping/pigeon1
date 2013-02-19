/**
 * 
 */
package com.dianping.dpsf.telnet.cmd;

import java.util.Map;
import java.util.Map.Entry;

import com.dianping.dpsf.stat.ServiceStat;
import com.dianping.dpsf.stat.StatBean;
import com.dianping.dpsf.telnet.TelnetCommandExecutor;

/**    
 * <p>    
 * Title: TelnetCommandState.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2011-3-1 上午11:47:06   
 */
public class TelnetCommandServiceStat implements TelnetCommandExecutor{
	
	private static TelnetCommandServiceStat stat = new TelnetCommandServiceStat();
	
	private String cmd = "-service";
	private String desc = "service DPSF service allCount failCount executeTime";
	
	public static TelnetCommandServiceStat getInstance(){
		return stat;
	}
	
	public String execute(String[] cmds) {
		StringBuffer sb = new StringBuffer();
		sb.append("**********client Service stat************\r\n\r\n");
		Map<String,StatBean> requestStat = ServiceStat.getClientServiceStat().getRequestStat();
		for(Entry<String,StatBean> entry : requestStat.entrySet()){
			sb.append(entry.getKey()).append("::::allCount:").append(entry.getValue().getCount())
			.append("    timeoutCount:").append(entry.getValue().getFailCount())
			.append("    lastExeTime:").append(entry.getValue().getLastExeTime()).append("ms\r\n");
		}
		sb.append("\r\n\r\n");
		sb.append("**********server Service stat************\r\n\r\n");
		requestStat = ServiceStat.getServerServiceStat().getRequestStat();
		if(requestStat != null && requestStat.size() > 0){
			for(Entry<String,StatBean> entry : requestStat.entrySet()){
				sb.append(entry.getKey()).append("::::allCount:").append(entry.getValue().getCount())
				.append("    timeoutCount:").append(entry.getValue().getFailCount())
				.append("    lastExeTime:").append(entry.getValue().getLastExeTime()).append("ms\r\n");
			}
		}else{
			sb.append("RequestExecutor is not init \r\n");
		}
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.telnet.TelnetCommandExecutor#getCmd()
	 */
	@Override
	public String getCmd() {
		return this.cmd;
	}

	/* (non-Javadoc)
	 * @see com.dianping.dpsf.telnet.TelnetCommandExecutor#getCmdInfo()
	 */
	@Override
	public String getCmdInfo() {
		return this.desc;
	}
	
}
