/**
 * 
 */
package com.dianping.dpsf;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * Title: LocalIP.java
 * </p>
 * <p>
 * Description: 描述
 * </p>
 * 
 * @author saber miao
 * @version 1.0
 * @created 2011-3-7 下午05:47:26
 */
public class LocalIP {
	private LocalIP() {
	}

	private static final Log log = LogFactory.getLog(LocalIP.class);

	private static String address;

	private static final String IP_ONLINE_PREFIX = "10.1.";

	private static final String IP_TEST_PREFIX = "192.168.";

	static {
		Enumeration<?> allNetInterfaces = null;
		try {
			allNetInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			log.error("", e);
		}
		InetAddress ip = null;
		first: while (allNetInterfaces.hasMoreElements()) {
			NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
			Enumeration<?> addresses = netInterface.getInetAddresses();
			while (addresses.hasMoreElements()) {
				ip = (InetAddress) addresses.nextElement();
				if (ip instanceof Inet4Address) {
					String ipValue = ip.getHostAddress();
					if (ipValue.indexOf(IP_ONLINE_PREFIX) == 0) {
						address = ipValue;
						break first;
					} else if (ipValue.indexOf(IP_TEST_PREFIX) == 0) {
						address = ipValue;
					}
				}
			}
		}
	}

	public static String getAddress() {
		return address;
	}

}
