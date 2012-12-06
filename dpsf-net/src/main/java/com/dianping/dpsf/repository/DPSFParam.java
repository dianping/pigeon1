/**
 * 
 */
package com.dianping.dpsf.repository;

/**
 * <p>
 * Title: DPSFParam.java
 * </p>
 * <p>
 * Description: 描述
 * </p>
 * 
 * @author saber miao
 * @version 1.0
 * @created 2010-10-28 下午04:33:00
 */
public class DPSFParam {

	private String[] paramNames;

	private int hashCode = 0;

	public DPSFParam(String[] paramNames) {
		this.paramNames = paramNames;

		StringBuffer sb = new StringBuffer();
		for (String paramName : paramNames) {
			sb.append(paramName).append("@");
		}
		this.hashCode = sb.toString().hashCode();
	}

	public int getLength() {
		return this.paramNames.length;
	}

	public String[] getParamNames() {
		return this.paramNames;
	}

	public int hashCode() {
		return this.hashCode;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof DPSFParam) {
			return this.hashCode == obj.hashCode();
		}
		return false;
	}

}
