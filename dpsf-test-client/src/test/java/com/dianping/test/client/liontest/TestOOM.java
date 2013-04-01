package com.dianping.test.client.liontest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Before;
import org.junit.Test;

import com.dianping.test.server.TestSupport;
import com.dianping.test.server.remote.TestOOMService;
import com.dianping.test.server.remote.TestParam;

public class TestOOM extends TestSupport{
	
	private String[] config = new String[]{"client-oom.xml"};
	
	public TestOOMService testProxy;
	
	private Map<String,TestParam> paramMap = new HashMap<String,TestParam>();

	@Override
	protected String[] getSpringConfig() {
		return config;
	}
	
	@Before
	public void before(){
		this.init();
		
		for(int i=0;i<10;i++){
			paramMap.put(""+i, new TestParam("a"+i,i));
		}
	}
	
	@Test
	public void testRemoteOOM(){
		AtomicLong count = new AtomicLong(0);
		long start = System.currentTimeMillis();
		while(true){
			for(int i=0;i<100;i++){
				try{
					testProxy.getResult();
					
					testProxy.getResult("a",1,new TestParam("b",2));
					
					testProxy.execute();
					
					testProxy.getResult(paramMap);
				}catch(Exception e){
					e.printStackTrace();
				}
				if(count.incrementAndGet()%10000 == 0){
					long now = System.currentTimeMillis();
					System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"+(count.get()*4000)/(now-start)+" 次/s &&&&&&&&&&&&");
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
