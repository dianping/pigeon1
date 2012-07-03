/**
 * 
 */
package com.dianping.dpsf.zookeeper;

import java.util.HashSet;
import java.util.Set;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import com.dianping.dpsf.thread.CycThreadPool;

/**    
 * <p>    
 * Title: ServicePublisher.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2011-3-7 下午03:25:42   
 */
public class ZooKeeperManager {
	
	private static ZooKeeperManager zkm;
	
	private ZooKeeper zk;
	
	private Set<String> serviceSet = new HashSet<String>();
	
	private boolean published = false;
	private boolean inited = false;
	
	
	
	private static final String DP = "/DP";
	private static final String DPSF = "/DPSF";
	private static final String SERVICE_PUBLISH = "/SERVICE_PUBLISH";
	private static final String SERVICE_SUBSCRIBE = "/SERVICE_SUBSCRIBE";
	private static final String HOSTS_WEIGHT = "/HOSTS_WEIGHT";
	
	public ZooKeeperManager(){
		init();
	}
	
	public static ZooKeeperManager getInstance(){
		if(zkm == null){
			zkm = new ZooKeeperManager();
		}
		return zkm;
	}
	
	
	private void init(){
		if(!this.inited){
//			this.zk = new ZooKeeper();
			this.inited = true;
		}
	}
	
	public void register(String serviceName){
		this.serviceSet.add(serviceName);
	}
	
	public void publish(){
		init();
		doPublish();
	}
	private void doPublish(){
		if(!this.published){
			
			
			this.published = true;
		}
	}
	
	class publishWatcher implements Watcher{

		/* (non-Javadoc)
		 * @see org.apache.zookeeper.Watcher#process(org.apache.zookeeper.WatchedEvent)
		 */
		@Override
		public void process(WatchedEvent event) {
			if(event.getType() == null){
				CycThreadPool.getPool().execute(new Runnable(){

					@Override
					public void run() {
						published = false;
						doPublish();
					}
					
				});
			}
		}
		
	}

}
