/**
 * 
 */
package com.dianping.dpsf.telnet;

import com.dianping.dpsf.exception.DPSFTelnetException;

/**
 * <p>
 * Title: TelnetCommand.java
 * </p>
 * <p>
 * Description: 描述
 * </p>
 * 
 * @author saber miao
 * @version 1.0
 * @created 2011-2-28 下午02:58:27
 */
public interface TelnetCommandExecutor {

	String execute(String[] cmds) throws DPSFTelnetException;

	String getCmd();

	String getCmdInfo();

}
