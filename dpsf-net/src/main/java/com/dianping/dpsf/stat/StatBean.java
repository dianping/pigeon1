/**
 * 
 */
package com.dianping.dpsf.stat;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.time.DateUtils;

/**    
 * <p>    
 * Title: ServiceStat.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2011-4-27 下午04:40:15   
 */
public class StatBean {
	
	private static long nextDay = -1;
	private AtomicLong count = new AtomicLong(0);
	private AtomicLong failCount = new AtomicLong(0);
	private int lastExeTime = 0;
	
	public void increment(){
		this.count.incrementAndGet();
	}
	
	public long getCount(){
		return this.count.get();
	}
	
	public void incrementFail(){
		this.failCount.incrementAndGet();
	}
	
	public long getFailCount(){
		return this.failCount.get();
	}
	
	public void setExeTime(long createTime){
		long now = System.currentTimeMillis();
		this.lastExeTime = (int)(now - createTime);
		if(now > nextDay){
			nextDay = DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DATE), 1).getTime();
			this.count = new AtomicLong(0);
			this.failCount = new AtomicLong(0);
		}
	}
	
	public int getLastExeTime(){
		return this.lastExeTime;
	}

}
