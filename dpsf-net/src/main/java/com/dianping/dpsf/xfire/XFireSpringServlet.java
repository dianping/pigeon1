package com.dianping.dpsf.xfire;



import org.codehaus.xfire.XFire;
import org.codehaus.xfire.transport.http.XFireServlet;

/**    
  * <p>    
  * Title: XFireSpringServlet.java   
  * </p>    
  * <p>    
  * Description: 描述  
  * </p>   
  * @author saber miao   
  * @version 1.0    
  * @created 2011-4-19 下午06:56:26   
  */ 
public class XFireSpringServlet
    extends XFireServlet{

    private static XFire xfire;

    public static void setXFire(XFire xf){
    	xfire = xf;
    }

    public XFire createXFire()
    {
        return xfire;
    }
    
}
