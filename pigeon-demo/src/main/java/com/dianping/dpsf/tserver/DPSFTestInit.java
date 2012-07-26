/**
 * 
 */
package com.dianping.dpsf.tserver;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.dom4j.DocumentException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**    
 * <p>    
 * Title: DPSFExampleInit.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-9-20 下午04:54:24   
 */
public class DPSFTestInit implements ServletContextListener{

	private static final String SERVICE_CONFIG_FILE = "tserver-beans.xml";
    static private Log LOGGER = LogFactory.getLog(DPSFTestInit.class);
    public static ApplicationContext beanFactory;
    static {
        LOGGER.warn("changing ROOT Logger.");
        DOMConfigurator.configure(Thread.currentThread()
                .getContextClassLoader().getResource("log4j.xml"));
        LOGGER.warn("changed ROOT Logger.");
    }


    public void contextDestroyed(final ServletContextEvent arg0) {
    }

    public void contextInitialized(final ServletContextEvent arg0) {
        try {
            beanFactory = new ClassPathXmlApplicationContext(BeansConfParser.classPathParse(SERVICE_CONFIG_FILE));
        } catch (final BeansException e) {
            LOGGER.error(e,e);
        } catch (final DocumentException e) {
            LOGGER.error(e,e);
        }

        LOGGER.info("DPSFExample started...");
    }
	
}
