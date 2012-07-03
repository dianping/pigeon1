/**
 * 
 */
package com.dianping.dpsf.telnet.cmd;

import java.util.Map;
import java.util.Map.Entry;

import com.dianping.dpsf.net.channel.manager.RouteManager;
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
public class TelnetCommandWeight implements TelnetCommandExecutor{
	
	private static TelnetCommandWeight weight = new TelnetCommandWeight();
	
	private RouteManager route;
	
	private String cmd = "-weight";
	private String desc = "weight DPSF set route weight\r\n" +
			"        example:-weight 127.0.0.1:2000 6 (for set 127.0.0.1:2000 weight to 6)\r\n" +
			"                -weight default          (for set to default)";
	
	public static TelnetCommandWeight getInstance(){
		return weight;
	}
	
	public String execute(String[] cmds) {
		StringBuffer sb = new StringBuffer();
		if(this.route != null){
			if(cmds.length == 1){
				Map<String, Map<String, Integer>> weights = this.route.getWeights();
				for(Entry<String,Map<String, Integer>> entry : weights.entrySet()){
					sb.append(entry.getKey()).append(">>>\r\n");
					for(Entry<String,Integer> e : entry.getValue().entrySet()){
						sb.append("          ").append(e.getKey()).append(":").append(e.getValue()).append("\r\n");
					}
				}
			}else if(cmds.length == 2){
				if("default".equalsIgnoreCase(cmds[1])){
					this.route.doDefault();
					sb.append("set default success\r\n");
				}else{
					sb.append("bad parameter:"+cmds[1]).append("\r\n");
					return sb.toString();
				}
			}else if(cmds.length == 3){
				int weight = 0;
				try{
					weight = Integer.parseInt(cmds[2]);
				}catch(Exception e){
					sb.append("bad parameter:"+cmds[2]).append("\r\n");
					return sb.toString();
				}
				if(weight > 10){
					sb.append("weight must be not over 10");
					return sb.toString();
				}
				if(this.route.setWeight(cmds[1], weight)){
					sb.append("set rout weight success\r\n");
				}else{
					sb.append("set rout weight fail and check parameter\r\n");
				}
			}
		}else{
			sb.append("route is not init \r\n");
		}
		return sb.toString();
	}


	/**
	 * @return the route
	 */
	public RouteManager getRoute() {
		return route;
	}

	/**
	 * @param route the route to set
	 */
	public void setRoute(RouteManager route) {
		this.route = route;
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
