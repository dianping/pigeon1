/**
 * 
 */
package com.dianping.dpsf.telnet.cmd;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import com.dianping.dpsf.exception.DPSFTelnetException;
import com.dianping.dpsf.telnet.TelnetCommandExecutor;
import com.dianping.dpsf.telnet.TelnetServer;

/**    
 * <p>    
 * Title: TelnetCommandCustom.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2011-3-1 下午02:37:37   
 */
public class TelnetCommandCustom implements BeanFactoryAware{

	private Map<String,String> cmds;
	
	private String username = "dpsf";
	
	private String password = "p0o9i8u7";
	
	private BeanFactory beanFactory;
	
	public void init() throws SecurityException, NoSuchMethodException, DPSFTelnetException{
		if(this.cmds != null){
			for(Entry<String,String> cmd : this.cmds.entrySet()){
				if(cmd.getKey().indexOf("-")==0){
					throw new DPSFTelnetException("service command can not have prefix \"-\"");
				}
				
				String[] values = cmd.getValue().split("@");
				final Object bean = beanFactory.getBean(values[0]);
				final Method method = bean.getClass().getMethod(values[1], new Class[0]);
				if(!(method.getReturnType() == String.class||method.getReturnType() == Void.class
						||method.getReturnType() == void.class)){
					throw new DPSFTelnetException("command method must return String or Void");
				}
				method.setAccessible(true);
				final String cmd_ = cmd.getKey();
				final String desc = values[2];
				CustomCommandInfo cci = new CustomCommandInfo(cmd.getKey(),values[0],values[1],values[2],
						new TelnetCommandExecutor(){

							@Override
							public String execute(String[] cmds) throws DPSFTelnetException {
								
								try {
									return method.invoke(bean, new Object[0]).toString();
								} catch (Exception e) {
									throw new DPSFTelnetException(e);
								} 
							}

							@Override
							public String getCmd() {
								return cmd_;
							}

							@Override
							public String getCmdInfo() {
								return desc;
							}
					
				});
				if(TelnetServer.getInstance().getServiceCmd().register(cmd.getKey(), cci)!= null){
					throw new DPSFTelnetException("command:"+cmd.getKey()+" has existed");
				}
			}
		}
		TelnetServer.getInstance().setUserName(this.username);
		TelnetServer.getInstance().setPassword(this.password);
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
	 */
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	/**
	 * @return the cmds
	 */
	public Map<String, String> getCmds() {
		return cmds;
	}

	/**
	 * @param cmds the cmds to set
	 */
	public void setCmds(Map<String, String> cmds) {
		this.cmds = cmds;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	

}
