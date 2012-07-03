/**
 * 
 */
package com.dianping.dpsf;

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RootLogger;
import org.apache.log4j.xml.DOMConfigurator;

import com.dianping.dpsf.control.PigeonConfig;
import com.dianping.hawk.Hawk;


/**    
 * <p>    
 * Title: DPSFLog.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-9-2 下午05:58:39   
 */
public class DPSFLog {
	
	static private final String LOGGER_NAME = "dpsf";
	static public final Log log = LogFactory.getLog(LOGGER_NAME);
	static public final Logger rootLogger = new RootLogger(Level.DEBUG);
	static private Logger LOGGER;
	static private volatile boolean initOK = false;
	static private int centralLogFactor;
	
	static{
		initDPSFLog();
	}

	static public synchronized void initDPSFLog() {
		if (initOK)
			return;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(
				DPSFLog.class.getClassLoader());
		
		Properties logPro = new Properties();
		String logLevel = "info";
		String logSuffix = "default";
		try {
			logPro.load(DPSFLog.class.getClassLoader().getResourceAsStream("config/applicationContext.properties"));
			logLevel = logPro.get("dpsf.logLevel")==null?null:logPro.get("dpsf.logLevel").toString();
			logSuffix = logPro.get("dpsf.logSuffix").toString();
		} catch (Exception e) {
			log.warn("not find dpsf.logSuffix from config/applicationContext.properties");
		}
		if(logSuffix == null || logSuffix.length() < 1){
			try{
				logSuffix = logPro.get("lion.app.prefix").toString();
			} catch (Exception e) {
				log.warn("not find lion.app.prefix from properties");
			}
		}
		
		LoggerRepository lr = new Hierarchy(rootLogger);
		
		new DOMConfigurator().doConfigure(DPSFLog.class.getClassLoader()
				.getResource("dpsf_log4j.xml"),lr);
		
		
		String osName = System.getProperty("os.name");
		String bizLogDir = null;
		if(osName != null && osName.toLowerCase().indexOf("windows")>-1){
			bizLogDir = "d:/";
		}
		FileAppender fileAppender = null;
		for (Enumeration<?> appenders = lr.getLogger(LOGGER_NAME).getAllAppenders(); (null == fileAppender)
				&& appenders.hasMoreElements();) {
			Appender appender = (Appender) appenders.nextElement();
			if (FileAppender.class.isInstance(appender)) {
				FileAppender logFileAppender=(FileAppender)appender;
				if(logLevel != null && logLevel.equalsIgnoreCase("debug")){
					logFileAppender.setThreshold(Level.DEBUG);
				}
				String logFileName = logFileAppender.getFile(); 
				File deleteFile=new File(logFileName);
				if(logSuffix != null){
					logFileName = logFileName.replace(".log", "."+logSuffix+".log");
				}
				if(bizLogDir != null){
					
					File logFile = new File(bizLogDir, logFileName);
					logFileName = logFile.getAbsolutePath();
				}
				if(logSuffix != null || bizLogDir != null){
					logFileAppender.setFile(logFileName);
					logFileAppender.activateOptions(); 
					if(deleteFile.exists()){
						deleteFile.delete();
					}
					log.warn(logFileAppender.getFile()+"的输出路径改变为:"+ logFileName);
				}
			}
		}
		
		
		Thread.currentThread().setContextClassLoader(loader);
		initOK = true;
		LOGGER = lr.getLogger(LOGGER_NAME);
		if(logLevel != null && logLevel.equalsIgnoreCase("debug")){
			LOGGER.setLevel(Level.DEBUG);
		}
		
	}
	
	public static void centralLogWarn(String key1, String key2, String key3) {
		centralLogWarn(key1, key2, key3, 1);
	}
	
	public static void centralLogWarn(String key1, String key2, String key3, double value) {
		centralLogWarn(key1, key2, key3, value, 10000);
	}
	
	public static void centralLogWarn(String key1, String key2, String key3, double value, long cacheSpan) {
		centralLog("@pigeon-warn", key1, key2, key3, value, cacheSpan);
	}
	
	public static void centralLogError(String key1, String key2, String key3) {
		centralLogError(key1, key2, key3, 1);
	}
	
	public static void centralLogError(String key1, String key2, String key3, double value) {
		centralLogError(key1, key2, key3, value, 10000);
	}
	
	public static void centralLogError(String key1, String key2, String key3, double value, long cacheSpan) {
		centralLog("@pigeon-error", key1, key2, key3, value, cacheSpan);
	}
	
	public static void centralLog(String key1, String key2, String key3, String key4, double value, long cacheSpan) {
		if (PigeonConfig.isHawkApiValid()) {
			try {
				Hawk.log(key1, key2, key3, key4, value, cacheSpan);
			} catch (Throwable e) {
				if (centralLogFactor++ < 10) {
					getLogger().warn("Log to log center failed, detail[" + e.getMessage() + "].");
				} else if (centralLogFactor >= 500) {
					centralLogFactor = 0;
				}
			}
		}
	}
	
	public static Logger getLogger(){
		if(LOGGER == null){
			initDPSFLog();
		}
		return LOGGER;
	}

}
