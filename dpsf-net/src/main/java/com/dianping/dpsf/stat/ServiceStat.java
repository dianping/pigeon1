/**
 * 
 */
package com.dianping.dpsf.stat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**    
 * <p>    
 * Title: ServiceStat.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2011-4-27 下午05:40:58
 * @deprecated   
 */
public class ServiceStat {
	
	private Map<String,StatBean> requestStat = new ConcurrentHashMap<String,StatBean>();
	
	public void countService(String serviceName){
		StatBean stat = this.requestStat.get(serviceName);
		if(stat == null){
			stat = new StatBean();
			this.requestStat.put(serviceName, stat);
		}
		stat.increment();
	}
	public void failCountService(String serviceName){
		StatBean stat = this.requestStat.get(serviceName);
		if(stat == null){
			stat = new StatBean();
			this.requestStat.put(serviceName, stat);
		}
		stat.incrementFail();
	}
	public void timeService(String serviceName,long createTime){
		StatBean stat = this.requestStat.get(serviceName);
		if(stat == null){
			stat = new StatBean();
			this.requestStat.put(serviceName, stat);
		}
		stat.setExeTime(createTime);
	}
	/**
	 * @return the requestStat
	 */
	public Map<String, StatBean> getRequestStat() {
		return requestStat;
	}

}
