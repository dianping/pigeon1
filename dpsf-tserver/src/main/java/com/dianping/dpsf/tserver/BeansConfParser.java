package com.dianping.dpsf.tserver;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


/**    
  * <p>    
  * Title: BeansConfParser.java   
  * </p>    
  * <p>    
  * Description: 描述  
  * </p>   
  * @author saber miao   
  * @version 1.0    
  * @created 2010-10-11 下午04:48:32   
  */ 
public class BeansConfParser {

    public static String[] classPathParse(final String xmlNameOfClassPath) throws DocumentException{
        final SAXReader reader = new SAXReader();
        final URL beansURL = BeansConfParser.class.getClassLoader().getResource(xmlNameOfClassPath);
        final Document document = reader.read(beansURL);
        final Element root = document.getRootElement();
        final List<String> beanList = new ArrayList<String>();
        for(Iterator<?> el=root.elementIterator();el.hasNext();){
            beanList.add(((Element)el.next()).getTextTrim());
        }
        final String[] beanConfs = new String[beanList.size()];
        return beanList.toArray(beanConfs); 
    }
    


}
