/**
 * 
 */
package com.dianping.dpsf.telnet;


/**    
 * <p>    
 * Title: TelnetConnectInfo.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2011-2-24 下午08:57:24   
 */
public class TelnetConnectInfo {
	
	private boolean checkUserName = false;
	
	private boolean checkPassword = false;
	
	private boolean checkSuperUser = false;
	
	private int checkUserNameNum = 0;
	
	private int checkPasswordNum = 0;

	/**
	 * @return the checkUserNameNum
	 */
	public int getCheckUserNameNum() {
		return checkUserNameNum;
	}
	
	public void incrementCheckUserNameNum(){
		this.checkUserNameNum++;
	}

	/**
	 * @return the checkPasswordNum
	 */
	public int getCheckPasswordNum() {
		return checkPasswordNum;
	}
	public void incrementCheckPasswordNum(){
		this.checkPasswordNum++;
	}

	/**
	 * @return the checkUserName
	 */
	public boolean isCheckUserName() {
		return checkUserName;
	}

	/**
	 * @param checkUserName the checkUserName to set
	 */
	public void setCheckUserName(boolean checkUserName) {
		this.checkUserName = checkUserName;
	}

	/**
	 * @return the checkPassword
	 */
	public boolean isCheckPassword() {
		return checkPassword;
	}

	/**
	 * @param checkPassword the checkPassword to set
	 */
	public void setCheckPassword(boolean checkPassword) {
		this.checkPassword = checkPassword;
	}

	/**
	 * @return the checkSuperUser
	 */
	public boolean isCheckSuperUser() {
		return checkSuperUser;
	}

	/**
	 * @param checkSuperUser the checkSuperUser to set
	 */
	public void setCheckSuperUser(boolean checkSuperUser) {
		this.checkSuperUser = checkSuperUser;
	}
	
}
