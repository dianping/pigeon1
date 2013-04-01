package com.dianping.test.server;

import java.lang.reflect.Field;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public abstract class TestSupport {
	
	
	protected static BeanFactory beanFactory = null;
	private boolean isInited = false;
	
	protected void init(){
		
		if(isInited){
			return;
		}
		
		beanFactory = new ClassPathXmlApplicationContext(getSpringConfig());
		Field[] fields = this.getClass().getFields();
		if(fields != null){
			for(Field field : fields){
				if(!field.getName().endsWith("beanFactory")){
					Object bean = beanFactory.getBean(field.getName());
					if(bean == null){
						throw new RuntimeException("not find bean that name is "+field.getName()+" from beanFactory");
					}
					field.setAccessible(true);
					try {
						field.set(this, bean);
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		System.out.println("*************************init TestSupport*********************");
		isInited = true;
	}
	
	protected abstract String[] getSpringConfig();

}
