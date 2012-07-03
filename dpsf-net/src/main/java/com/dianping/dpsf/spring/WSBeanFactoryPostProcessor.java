/**
 * 
 */
package com.dianping.dpsf.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

/**    
 * <p>    
 * Title: WSBeanFactoryPostProcessor.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2011-5-30 下午07:28:30   
 */
public class WSBeanFactoryPostProcessor implements ApplicationContextAware, BeanFactoryPostProcessor{

	private ConfigurableApplicationContext applicationContext;
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.config.BeanFactoryPostProcessor#postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
	 */
	@Override
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
		BeanDefinitionRegistry d = null;
		XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader((BeanDefinitionRegistry)this.applicationContext.getBeanFactory());  
		beanDefinitionReader.setResourceLoader(this.applicationContext);  
		beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this.applicationContext));  
		beanDefinitionReader.loadBeanDefinitions(this.applicationContext.getResource("org/codehaus/xfire/spring/xfire.xml"));
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = (ConfigurableApplicationContext)applicationContext;
	}
	

}
