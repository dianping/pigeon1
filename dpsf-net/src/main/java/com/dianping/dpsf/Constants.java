/**
 * 
 */
package com.dianping.dpsf;

/**    
 * <p>    
 * Title: Constants.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2010-8-12 下午10:46:31   
 */
public class Constants {
	private Constants() {}
	
	public static final int MESSAGE_TYPE_HEART = 1; 
	public static final int MESSAGE_TYPE_SERVICE = 2;
	public static final int MESSAGE_TYPE_EXCEPTION = 3;
	public static final int MESSAGE_TYPE_SERVICE_EXCEPTION = 4;
	
	public static final byte SERILIZABLE_PB = 1;
	public static final byte SERILIZABLE_HESSIAN = 2;
	public static final byte SERILIZABLE_JAVA = 3;
	public static final byte SERILIZABLE_THRIFT = 4;
	public static final byte SERILIZABLE_NO = 5;
	public static final byte SERILIZABLE_HESSIAN1 = 6;		//hessian spec. 1.0, spec 2.0兼容1.0，但1.0不兼容2.0
	
	public static final int CALLTYPE_REPLY = 1;
	public static final int CALLTYPE_NOREPLY = 2;
	

	public static final String CALL_SYNC = "sync";
	public static final String CALL_CALLBACK = "callback";
	public static final String CALL_ONEWAY = "oneway";
	public static final String CALL_FUTURE = "future";
	
	public static final String SERIALIZE_PB = "pb";
	public static final String SERIALIZE_JAVA = "java";
	public static final String SERIALIZE_HESSIAN = "hessian";
	public static final String SERIALIZE_THRIFT = "thrift";
	public static final String SERIALIZE_WS = "ws";
	
	
	public static final byte MESSAGE_HEAD_FIRST = 57;
	public static final byte MESSAGE_HEAD_SECOND = 58;
	public static final byte[] MESSAGE_HEAD = new byte[]{MESSAGE_HEAD_FIRST,MESSAGE_HEAD_SECOND};
	
	public static final byte EXPAND_FLAG_FIRST = 29;
	public static final byte EXPAND_FLAG_SECOND = 30;
	public static final byte EXPAND_FLAG_THIRD = 31;
	public static final byte[] EXPAND_FLAG= new byte[]{EXPAND_FLAG_FIRST,EXPAND_FLAG_SECOND,EXPAND_FLAG_THIRD};
	
	public static final int ATTACHMENT_RETRY = 1;
	public static final int ATTACHMENT_BYTEBUFFER = 2;
	public static final int ATTACHMENT_IS_TELNET = 3;
	public static final int ATTACHMENT_CURRENT_INDEX = 4;
	public static final int ATTACHMENT_TELNET_INFO = 5;
	public static final int ATTACHMENT_REQUEST_SEQ = 11;
	
	public static final String TRANSFER_NULL = "NULL";
	
	public static final String TELNET_CHARSET = "UTF-8";
	
	public static final String REQ_ATTACH_FLOW = "FLOW";
	public static final String REQ_ATTACH_CLIENT_WEIGHT = "CLIENT_WEIGHT";
	public static final String REQ_ATTACH_WRITE_BUFF_LIMIT = "WRITE_BUFF_LIMIT";
	
	public static final int VERSION_150 = 150;
	
	public static final String REQUEST_CREATE_TIME = "requestCreateTime";
	public static final String REQUEST_TIMEOUT = "requestTimeout";
	
}
