/**
 * 
 */
package com.dianping.dpsf.telnet;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.DPSFLog;
import com.dianping.dpsf.DPSFUtils;
import com.dianping.dpsf.telnet.cmd.TelnetCommandServiceStat;
import com.dianping.dpsf.telnet.cmd.TelnetCommandState;
import com.dianping.dpsf.telnet.cmd.TelnetCommandWeight;
import com.dianping.dpsf.thread.DPSFThreadPool;
import com.dianping.dpsf.thread.ExeThreadPool;

/**    
 * <p>    
 * Title: TelnetServer.java   
 * </p>    
 * <p>    
 * Description: 描述  
 * </p>   
 * @author saber miao   
 * @version 1.0    
 * @created 2011-2-24 下午03:21:39   
 */
public class TelnetServer {
	
	private static Logger logger = DPSFLog.getLogger();
	
	private static TelnetServer tc = new TelnetServer();
	
	private TelnetDPSFCommand dpsfCmd;
	private TelnetServiceCommand serviceCmd;
	
	
	
	private static byte[] loginBytes;
	private static byte[] byeBytes;
	
	private static final byte CR = 13;
	
	private static final byte LF = 10;
	
	private String userName = "test";
	private String password = "12qwaszx";
	private String superUserName = "dpsf";
	private String superPassword = "p0o9i8u7";
	public TelnetServer(){
		
		this.loginBytes = new byte[]{45,108,111,103,105,110,13,10};
		this.byeBytes = new byte[]{45,98,121,101,13,10};
		this.dpsfCmd = new TelnetDPSFCommand();
		this.serviceCmd = new TelnetServiceCommand();
		this.dpsfCmd.registerState(TelnetCommandState.getInstance());
		this.dpsfCmd.registerState(TelnetCommandServiceStat.getInstance());
		this.dpsfCmd.registerState(TelnetCommandWeight.getInstance());
	}
	
	public static TelnetServer getInstance(){
		return tc;
	}
	
	public boolean isTelnet(ChannelHandlerContext ctx, Channel channel,ChannelBuffer cb){
		if(isBye(ctx, channel,cb)){
			return true;
		}
		return isLogin(ctx, channel,cb);
	}
	
	public boolean isBye(ChannelHandlerContext ctx, Channel channel,ChannelBuffer cb){
		int currentIndex = cb.readerIndex();
		cb.readerIndex(0);

		int currentByteIndex = 0;
		while(cb.readableBytes() >= 6 - currentByteIndex){
			if(cb.readByte() == byeBytes[currentByteIndex]){
				if(++currentByteIndex == 6){
					channel.close();
					return true;
				}
			}else{
				currentByteIndex = 0;
			}
		}
		cb.readerIndex(currentIndex);
		return false;
	}
	
	public boolean isLogin(ChannelHandlerContext ctx, Channel channel,ChannelBuffer cb){
		int currentIndex = cb.readerIndex();
		cb.readerIndex(0);

		int currentByteIndex = 0;
		while(cb.readableBytes() >= 8 - currentByteIndex){
			if(cb.readByte() == loginBytes[currentByteIndex]){
				if(++currentByteIndex == 8){
					channel.write("username>>");
					DPSFUtils.setAttachment(ctx, Constants.ATTACHMENT_IS_TELNET,true);
					DPSFUtils.setAttachment(ctx, Constants.ATTACHMENT_TELNET_INFO,new TelnetConnectInfo());
					return true;
				}
			}else{
				currentByteIndex = 0;
			}
		}
		cb.readerIndex(currentIndex);
		return false;
	}
	
	public boolean executeCMD(ChannelHandlerContext ctx,Channel channel,ChannelBuffer cb){
		int oldIndex = cb.readerIndex();
		String cmd = null;
		
		while(cb.readable()){
			if(cb.readByte() == CR){
				if(cb.readByte() == LF){
					try {
						cmd = new String(cb.copy(oldIndex, cb.readerIndex()-oldIndex-2).array(),Constants.TELNET_CHARSET);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					cb.readerIndex(cb.readerIndex()+cb.readableBytes());
				}else{
					cb.readerIndex(cb.readerIndex()-1);
				}
			}
		}
		if(cmd == null){
			return false;
		}
		
		if(cmd.equals("-bye")){
			channel.close();
		}
		TelnetConnectInfo tci = (TelnetConnectInfo)DPSFUtils.getAttachmentNotRemove(ctx, Constants.ATTACHMENT_TELNET_INFO);

		if(!tci.isCheckUserName()){
			tci.incrementCheckUserNameNum();
			if(cmd.equals(this.userName)){
				tci.setCheckUserName(true);
				channel.write("password>>");
			}else if(cmd.equals(this.superUserName)){
				tci.setCheckUserName(true);
				tci.setCheckSuperUser(true);
				channel.write("password>>");
			}else{
				if(tci.getCheckUserNameNum()<3){
					channel.write("username is error\r\nusername>>");
				}else{
					channel.write("error username has been 3 times\r\n");
					channel.close();
				}
			}
			
			return true;
		}else if(!tci.isCheckPassword()){
			tci.incrementCheckPasswordNum();
			if((!tci.isCheckSuperUser() && cmd.equals(this.password))||(tci.isCheckSuperUser() && cmd.equals(this.superPassword))){
				tci.setCheckPassword(true);
				channel.write("**** welcome "+(tci.isCheckSuperUser() ? this.superUserName : this.userName)+" ****\r\n");
				channel.write("**** you can use \"-help\" to see command info and \"-bye\" to exit ****\r\n>>");
			}else{
				if(tci.getCheckPasswordNum()<3){
					channel.write("password is error\r\npassword>>");
				}else{
					channel.write("error password has been 3 times\r\n");
					channel.close();
				}
			}
			
			return true;
		}
		InetSocketAddress address = (InetSocketAddress)channel.getRemoteAddress();
		logger.info("client telent>>>>"+address.getHostName()+" "+cmd);
		if(cmd.equals("-help")){
			StringBuffer sb = new StringBuffer();
			sb.append("***************************************************************\r\n");
			sb.append("***************************************************************\r\n");
			sb.append("** -login\r\n");
			sb.append("**      login(you must login before other command);\r\n");
			sb.append("** -bye\r\n");
			sb.append("**      exit;\r\n");
			sb.append("** -help\r\n");
			sb.append("**      help;\r\n");
			sb.append(this.dpsfCmd.getCMDInfo());
			sb.append(this.serviceCmd.getCMDInfo());
			sb.append("***************************************************************\r\n");
			sb.append("***************************************************************\r\n");
			sb.append(">>");
			channel.write(sb.toString());
			return true;
		}
		String[] cmds = cmd.split(" ");
		if(cmds.length >= 2 && cmds[0].equalsIgnoreCase("-weight") && !tci.isCheckSuperUser()){
			channel.write("you have not permission for this command\r\n>>");
			return true;
		}
		if(!this.dpsfCmd.execute(channel, cmds)){
			if(cmd.indexOf("-")==0){
				channel.write("service command can not have prefix:\"-\"\r\n>>");
			}
			if(!this.serviceCmd.execute(channel, cmds)){
				channel.write("no this command:"+cmd+"\r\n>>");
			}
		}
		return true;
	}

	/**
	 * @return the dpsfCmd
	 */
	public TelnetDPSFCommand getDpsfCmd() {
		return dpsfCmd;
	}

	/**
	 * @return the serviceCmd
	 */
	public TelnetServiceCommand getServiceCmd() {
		return serviceCmd;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	

}
