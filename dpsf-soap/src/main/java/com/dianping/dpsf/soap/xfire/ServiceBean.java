/**
 * 
 */
package com.dianping.dpsf.soap.xfire;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.service.invoker.BeanInvoker;
import org.codehaus.xfire.service.invoker.Invoker;
import org.codehaus.xfire.service.invoker.ObjectInvoker;
import org.codehaus.xfire.spring.SpringUtils;
import org.codehaus.xfire.spring.config.AbstractSoapBindingBean;
import org.codehaus.xfire.spring.config.EndpointBean;
import org.codehaus.xfire.spring.config.Soap11BindingBean;
import org.codehaus.xfire.spring.config.Soap12BindingBean;
import org.codehaus.xfire.spring.config.SpringServiceConfiguration;
import org.codehaus.xfire.util.Resolver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**    
  * <p>    
  * Title: ServiceBean.java   
  * </p>    
  * <p>    
  * Description: 描述  
  * </p>   
  * @author saber miao   
  * @version 1.0    
  * @created 2011-4-19 下午06:42:01   
  */ 
public class ServiceBean{
    private final static Log logger = LogFactory.getLog(ServiceBean.class);

    private Service xfireService;

    private ServiceFactory serviceFactory;

    protected XFire xFire;

    private String name;

    private String namespace;

    private Class serviceInterface;

    private Object service;

    private List bindings;

    private List inHandlers;

    private List outHandlers;

    private List faultHandlers;

    private List schemas;
    
    protected Class implementationClass;

    private Map properties = new HashMap();

    private String wsdlURL;
    
    private List operations;
    
    /** Some properties to make it easier to work with ObjectServiceFactory */

    protected boolean createDefaultBindings = true;

    protected String use;

    protected String style;

    private String scope;
    
    private Invoker invoker;
    
    private Object executor;

    public void init()
        throws Exception{
        // Use specific name if given, else fall back to bean name.
//        String theName = (this.name != null ? this.name : this.beanName);
//        if (theName != null && theName.startsWith("/"))
//        {
//            theName = theName.substring(1);
//        }
//        
        if (serviceFactory == null){
            serviceFactory = new ObjectServiceFactory(xFire.getTransportManager(),
                                                      new AegisBindingProvider());
        }

        ObjectServiceFactory osf = (ObjectServiceFactory) serviceFactory;

        SpringServiceConfiguration springConfig = new SpringServiceConfiguration();
        springConfig.setMethods(operations);
        osf.getServiceConfigurations().add(0, springConfig);


        /**
         * Use the ServiceInterface if that is set, otherwise use the Class of 
         * the service object.
         */
        Class intf = getServiceClass();
        if (intf == null){
            if (getServiceBean() == null)
                throw new RuntimeException("Error creating service " + name +
                        ". The service class or the service bean must be set!");
            
            intf = SpringUtils.getUserTarget(getServiceBean()).getClass();
        }
        
        // Lets set up some properties for the service
        if (createDefaultBindings)
            	properties.put(ObjectServiceFactory.CREATE_DEFAULT_BINDINGS, Boolean.TRUE);
        else
        		properties.put(ObjectServiceFactory.CREATE_DEFAULT_BINDINGS, Boolean.FALSE);
        
        if (style != null)
            properties.put(ObjectServiceFactory.STYLE, style);
        if (use != null)
            properties.put(ObjectServiceFactory.USE, use);
        if (scope != null)
            properties.put(ObjectServiceFactory.SCOPE, scope);
        
        if (implementationClass != null){
            properties.put(ObjectInvoker.SERVICE_IMPL_CLASS, implementationClass);
        }

        if (schemas != null){
            properties.put(ObjectServiceFactory.SCHEMAS, schemas);
        }
        
        if (wsdlURL != null){
            String home = (String) getXfire().getProperty(XFire.XFIRE_HOME);
            
            Resolver resolver = new Resolver(home, wsdlURL);
            
            URL url = resolver.getURL();
            if (url == null){
                throw new XFireRuntimeException("Could not resolve WSDL URL " + wsdlURL);
            }
            
            xfireService = serviceFactory.create(intf, null, url, properties);
        }else{
            xfireService = serviceFactory.create(intf, name, namespace, properties);
        }
        xfireService.setExecutor(executor);
        
        // dirty hack to remove our ServiceConfiguration
        osf.getServiceConfigurations().remove(springConfig);
        
        if (bindings != null && serviceFactory instanceof ObjectServiceFactory){
            initializeBindings();
        }
        
        if (logger.isInfoEnabled()){
            logger.info("Exposing service with name " + xfireService.getName());
        }

        if (invoker != null){
            xfireService.setInvoker(invoker);
        }else{
            // If we're referencing a spring bean, set up our invoker.
            Object serviceBean = getProxyForService();
            if (serviceBean != null){
                xfireService.setInvoker(new BeanInvoker(serviceBean));
            }
        }
        
        // set up in handlers
        if (xfireService.getInHandlers() == null)
            xfireService.setInHandlers(getInHandlers());
        else if (getInHandlers() != null)
            xfireService.getInHandlers().addAll(getInHandlers());

        // set up out handlers
        if (xfireService.getOutHandlers() == null)
            xfireService.setOutHandlers(getOutHandlers());
        else if (getOutHandlers() != null)
            xfireService.getOutHandlers().addAll(getOutHandlers());

        // set up fault handlers.
        if (xfireService.getFaultHandlers() == null)
            xfireService.setFaultHandlers(getFaultHandlers());
        else if (getFaultHandlers() != null)
            xfireService.getFaultHandlers().addAll(getFaultHandlers());
        // Register the service
        xFire.getServiceRegistry().register(xfireService);
    }

    protected void initializeBindings()
        throws Exception{
        ObjectServiceFactory osf = (ObjectServiceFactory) serviceFactory;

        for (Iterator itr = bindings.iterator(); itr.hasNext();){
            AbstractSoapBindingBean o = (AbstractSoapBindingBean) itr.next();
            org.codehaus.xfire.soap.AbstractSoapBinding binding = null;
            if (o instanceof Soap11BindingBean){
                binding = osf.createSoap11Binding(xfireService, o.getName(), o.getTransport());
            }else if (o instanceof Soap12BindingBean){
                binding = osf.createSoap12Binding(xfireService, o.getName(), o.getTransport());
            }
            
            binding.setUndefinedEndpointAllowed(o.isAllowUndefinedEndpoints());

            if (o.getEndpoints() == null) continue;
            
            for (Iterator eitr = o.getEndpoints().iterator(); eitr.hasNext();){
                EndpointBean ep = (EndpointBean) eitr.next();
                
                osf.createEndpoint(xfireService, ep.getName(), ep.getUrl(), binding);
            }
        }
    }

    /**
     * @return
     */
    protected Object getProxyForService(){
        return getServiceBean();
    }

    /**
     * Gets the XFire Service created by this bean.
     * @return
     * @see Service
     */
    public Service getXFireService(){
        return xfireService;
    }

    /**
     * Gets the object which backs this service.
     * @return
     */
    public Object getServiceBean(){
        return service;
    }

    /**
     * Sets the object which backs this service.
     * @return
     */
    public void setServiceBean(Object service){
        this.service = service;
    }

    /**
     * Set the service class. The service class is passed to the ServiceFactory's
     * create method and is used to determine the operations on the service.
     * @return
     */
    public Class getServiceClass(){
        return serviceInterface;
    }

    public void setServiceClass(Class serviceInterface){
        this.serviceInterface = serviceInterface;
    }

    public void setServiceFactory(ServiceFactory serviceFactory){
        this.serviceFactory = serviceFactory;
    }

    public ServiceFactory getServiceFactory(){
        return this.serviceFactory;
    }

    /**
     * Sets the service name. Default is the bean name of this exporter.
     */
    public void setName(String name){
        this.name = name.trim();
    }

    /**
     * Sets the service default namespace. Default is a namespace based on the
     * package of the {@link #getServiceClass() service interface}.
     */
    public void setNamespace(String namespace){
        this.namespace = namespace.trim();
    }

    public List getFaultHandlers(){
        return faultHandlers;
    }

    public void setFaultHandlers(List faultHandlers){
        this.faultHandlers = faultHandlers;
    }

    public List getInHandlers(){
        return inHandlers;
    }

    public void setInHandlers(List inHandlers){
        this.inHandlers = inHandlers;
    }

    public List getOutHandlers(){
        return outHandlers;
    }

    public void setOutHandlers(List outHandlers){
        this.outHandlers = outHandlers;
    }

    public void setXfire(XFire xFire){
        this.xFire = xFire;
    }

    public XFire getXfire(){
        return xFire;
    }

    public Class getImplementationClass(){
        return implementationClass;
    }

    public void setImplementationClass(Class implementationClass){
        this.implementationClass = implementationClass;
    }
    
    /**
     * @org.xbean.Map entryName="property" keyName="key"
     * @return
     */
    public Map getProperties(){
        return properties;
    }

    public void setProperties(Map properties){
        this.properties = properties;
    }

    public String getScope(){
        return scope;
    }

    public void setScope(String scope){
        this.scope = scope;
    }

    public String getStyle(){
        return style;
    }

    public void setStyle(String style){
        this.style = style;
    }

    public String getUse(){
        return use;
    }

    public void setUse(String use){
        this.use = use;
    }

    public List getSchemas(){
        return schemas;
    }

    public void setSchemas(List schemas){
        this.schemas = schemas;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext ctx)
        throws BeansException{
        if (xFire == null) xFire = (XFire) ctx.getBean("xfire");
        if (serviceFactory == null) serviceFactory = (ServiceFactory) ctx.getBean("xfire.serviceFactory");
    }

    public List getBindings(){
        return bindings;
    }

    public void setBindings(List bindings){
        this.bindings = bindings;
    }

    public Invoker getInvoker(){
        return invoker;
    }

    public void setInvoker(Invoker invoker){
        this.invoker = invoker;
    }

    public boolean isCreateDefaultBindings(){
        return createDefaultBindings;
    }

    public Object getExecutor(){
        return executor;
    }

    public void setExecutor(Object executor){
        this.executor = executor;
    }

    public void setCreateDefaultBindings(boolean createDefaultBindings){
        this.createDefaultBindings = createDefaultBindings;
    }

    public String getWsdlURL(){
        return wsdlURL;
    }

    public void setWsdlURL(String wsdlURL){
        this.wsdlURL = wsdlURL;
    }

    /**
     * @org.apache.xbean.FlatCollection childElement="method"
     * @return
     */
    public List getMethods(){
        return operations;
    }

    public void setMethods(List operations){
        this.operations = operations;
    }

}