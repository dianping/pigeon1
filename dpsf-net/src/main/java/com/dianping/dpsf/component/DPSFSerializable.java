/**
 * 
 */
package com.dianping.dpsf.component;

import java.io.Serializable;

import com.dianping.dpsf.exception.NetException;

/**
 * <p>
 * Title: DPSFSerializable.java
 * </p>
 * <p>
 * Description: 描述
 * </p>
 * 
 * @author saber miao
 * @version 1.0
 * @created 2010-8-19 下午03:23:51
 */
public interface DPSFSerializable extends Serializable {

	byte getSerializ();

	void setSequence(long seq);

	long getSequence();

	Object getObject() throws NetException;

	Object getContext();

	void setContext(Object context);

}
