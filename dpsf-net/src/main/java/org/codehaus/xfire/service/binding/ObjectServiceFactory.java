package org.codehaus.xfire.service.binding;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.namespace.QName;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.XFireRuntimeException;
import org.codehaus.xfire.exchange.MessageSerializer;
import org.codehaus.xfire.fault.FaultInfoException;
import org.codehaus.xfire.fault.FaultSender;
import org.codehaus.xfire.fault.SoapFaultSerializer;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.CustomFaultHandler;
import org.codehaus.xfire.handler.OutMessageSender;
import org.codehaus.xfire.service.Binding;
import org.codehaus.xfire.service.Endpoint;
import org.codehaus.xfire.service.FaultInfo;
import org.codehaus.xfire.service.MessageInfo;
import org.codehaus.xfire.service.MessagePartContainer;
import org.codehaus.xfire.service.MessagePartInfo;
import org.codehaus.xfire.service.OperationInfo;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.service.ServiceInfo;
import org.codehaus.xfire.service.binding.BindingProvider;
import org.codehaus.xfire.service.binding.PostInvocationHandler;
import org.codehaus.xfire.service.binding.ServiceConfiguration;
import org.codehaus.xfire.service.binding.ServiceInvocationHandler;
import org.codehaus.xfire.service.documentation.DocumentationProvider;
import org.codehaus.xfire.service.documentation.XMLDocumentationBuilder;
import org.codehaus.xfire.service.invoker.ObjectInvoker;
import org.codehaus.xfire.service.invoker.ScopePolicyEditor;
import org.codehaus.xfire.soap.AbstractSoapBinding;
import org.codehaus.xfire.soap.Soap11Binding;
import org.codehaus.xfire.soap.Soap12Binding;
import org.codehaus.xfire.soap.SoapConstants;
import org.codehaus.xfire.soap.SoapTransport;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.transport.http.SoapHttpTransport;
import org.codehaus.xfire.transport.local.LocalTransport;
import org.codehaus.xfire.util.ClassLoaderUtils;
import org.codehaus.xfire.util.MethodComparator;
import org.codehaus.xfire.util.NamespaceHelper;
import org.codehaus.xfire.util.ServiceUtils;
import org.codehaus.xfire.wsdl.ResourceWSDL;
import org.codehaus.xfire.wsdl.WSDLWriter;
import org.codehaus.xfire.wsdl11.DefinitionWSDL;
import org.codehaus.xfire.wsdl11.ResolverWSDLLocator;
import org.codehaus.xfire.wsdl11.builder.DefaultWSDLBuilderFactory;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilderAdapter;
import org.codehaus.xfire.wsdl11.builder.WSDLBuilderFactory;
import org.codehaus.xfire.wsdl11.parser.WSDLServiceConfigurator;
import org.xml.sax.InputSource;


/**
 * Creates Services from java objects. This class is meant to be easily overridable
 * so you can customize how your services are created. 
 *  
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class ObjectServiceFactory
        implements ServiceFactory
{
    public static final String PORT_TYPE = "objectServiceFactory.portType";
    public static final String PORT_NAME = "objectServiceFactory.portName";
    public static final String STYLE = "objectServiceFactory.style";
    public static final String USE = "objectServiceFactory.use";
    public static final String CREATE_DEFAULT_BINDINGS =  "objectServiceFactory.createDefaultBindings";
    public static final String SOAP11_TRANSPORTS =  "objectServiceFactory.soap11Transports";
    public static final String SOAP12_TRANSPORTS =  "objectServiceFactory.soap12Transports";
    public static final String SCOPE = "objectServiceFactory.scope";
    public static final String SCHEMAS = "objectServiceFactory.schemas";
    
    private BindingProvider bindingProvider;
    private TransportManager transportManager;
    private String style;
    private String use;
    private Set ignoredClasses = new HashSet();
    private boolean voidOneWay;
    private WSDLBuilderFactory wsdlBuilderFactory = new DefaultWSDLBuilderFactory();
    private boolean customFaultsEnabled = true;
    private boolean bindingCreationEnabled = true;
    private static final DocumentationProvider EMPTY_DOC_PROVIDER = new DocumentationProvider();
    private DocumentationProvider documentationProvider = EMPTY_DOC_PROVIDER;
    private Map customTypesMapping = new HashMap();
    
    private Set soap11Transports = new HashSet();
    private Set soap12Transports = new HashSet();
    
    private List serviceConfigurations = new ArrayList();
    
    /**
     * Initializes a new instance of the <code>ObjectServiceFactory</code>.
     * Uses the XFireFactory to obtain an instance of the TransportManager.
     */
    public ObjectServiceFactory()
    {
        this(XFireFactory.newInstance().getXFire().getTransportManager());
    }

    public Map getCustomTypesMapping() {
		return customTypesMapping;
	}

	public void setCustomTypesMapping(Map customTypesMapping) {
		this.customTypesMapping = customTypesMapping;
	}

	/**
     * Initializes a new instance of the <code>ObjectServiceFactory</code> with the given transport manager and type
     * mapping registry.
     *
     * @param transportManager the transport manager
     * @param provider         the binding provider
     */
    public ObjectServiceFactory(TransportManager transportManager, BindingProvider provider)
    {
        this(transportManager);

        this.bindingProvider = provider;
    }

    /**
     * Initializes a new instance of the <code>ObjectServiceFactory</code>.
     */
    public ObjectServiceFactory(TransportManager transportManager)
    {
        this.transportManager = transportManager;
        setStyle(SoapConstants.STYLE_WRAPPED);
        setUse(SoapConstants.USE_LITERAL);
        
        DefaultServiceConfiguration config = new DefaultServiceConfiguration();
        config.setServiceFactory(this);
        serviceConfigurations.add(config);
        
        soap11Transports.add(SoapHttpTransport.SOAP11_HTTP_BINDING);
        soap11Transports.add(LocalTransport.BINDING_ID);
        soap12Transports.add(LocalTransport.BINDING_ID);

        ignoredClasses.add("java.lang.Object");
        ignoredClasses.add("java.lang.Throwable");
        ignoredClasses.add("org.omg.CORBA_2_3.portable.ObjectImpl");
        ignoredClasses.add("org.omg.CORBA.portable.ObjectImpl");
        ignoredClasses.add("javax.ejb.EJBObject");
        ignoredClasses.add("javax.rmi.CORBA.Stub");
    }

    public ObjectServiceFactory(BindingProvider bp)
    {
        this(XFireFactory.newInstance().getXFire().getTransportManager(), bp);
    }
    
    public BindingProvider getBindingProvider()
    {
        if (bindingProvider == null)
        {
            try
            {
                bindingProvider = (BindingProvider) ClassLoaderUtils
                        .loadClass("org.codehaus.xfire.aegis.AegisBindingProvider", getClass()).newInstance();
            }
            catch (Exception e)
            {
                throw new XFireRuntimeException("Couldn't find a binding provider!", e);
            }
        }

        return bindingProvider;
    }

    /**
     * Creates a service via <code>create(Class)</code>. It then configures
     * the bindings and endpoints on the service via the WSDL. 
     */
    public Service create(Class clazz, QName name, URL wsdlUrl, Map properties)
    {
        try
        {
            return create(clazz, 
                          name, 
                          WSDLFactory.newInstance().newWSDLReader().readWSDL(new ResolverWSDLLocator(null, new InputSource(wsdlUrl.openStream()))), 
                          properties,
                          new ResourceWSDL(wsdlUrl));
        }
        catch (WSDLException e)
        {
            throw new XFireRuntimeException("Could not load WSDL.", e);
        }
        catch (IOException e)
        {
            throw new XFireRuntimeException("Could not load WSDL.", e);
        }
    }
    

    public Service create(Class clazz, QName name, Definition def, Map properties)
    {
    	return create(clazz, name, def, properties, new DefinitionWSDL(def));
    }
    
    public Service create(Class clazz, QName name, Definition def, Map properties, WSDLWriter writer)
    {
        if (properties == null) properties = new HashMap();
        
        properties.put(CREATE_DEFAULT_BINDINGS, Boolean.FALSE);
        
        if (name == null)
        {
            Map services = def.getServices();
            javax.wsdl.Service service = (javax.wsdl.Service) getOnlyElem(services);
            if (service != null)
            {
                 name = service.getQName();
            }
        }
        
        Service service;
        if (name != null)
            service = create(clazz, name.getLocalPart(), name.getNamespaceURI(), properties);
        else
            service = create(clazz, properties);
        
        if (name != null) service.setName(name);
        
        service.setWSDLWriter(writer);

        try
        {
            WSDLServiceConfigurator config = new WSDLServiceConfigurator(service, def, transportManager);
            config.configure();
        }
        catch (Exception e)
        {
            if (e instanceof XFireRuntimeException)
                throw (XFireRuntimeException) e;
            
            throw new XFireRuntimeException("Couldn't configure service.", e);
        }
        
        for (Iterator itr = service.getBindings().iterator(); itr.hasNext();)
        {
            Binding b = (Binding) itr.next();
            
            for (Iterator oitr = service.getServiceInfo().getOperations().iterator(); oitr.hasNext();)
            {
                OperationInfo op = (OperationInfo) oitr.next();
                
                configureHeaders(service, op, b);
            }
            
            if (b instanceof AbstractSoapBinding)
            {
                b.setSerializer(getSerializer((AbstractSoapBinding) b));
            }
            
            service.getBindingProvider().initialize(service, b);
        }
        
        service.getBindingProvider().initialize(service);
        
        return service;
    }
    
    /**
     * Returns the only value in a Map
     * 
     * @param map
     * @return the only value in the map, if it contained exactly 1 key/value
     *         pair <br>
     *         <code>null</code>, otherwise <br>
     */
    private Object getOnlyElem(Map map)
    {
        if (map.size() == 1)
        {
            Set keySet = map.keySet();
            Iterator i = keySet.iterator();
            return map.get(i.next());
        }
        else
        {
            return null;
        }
    }
    
    protected void configureHeaders(Service service, OperationInfo op, Binding b)
    {
        Method method = op.getMethod();
        Class[] paramClasses = method.getParameterTypes();
        MessagePartContainer inHeaders = b.getHeaders(op.getInputMessage());
        MessagePartContainer outHeaders = null;
        if (op.hasOutput()) outHeaders = b.getHeaders(op.getOutputMessage());
        
        for (int j = 0; j < paramClasses.length; j++)
        {
            if (!paramClasses[j].equals(MessageContext.class) && isHeader(method, j))
            {
                final QName q = getInParameterName(service, op, method, j, false);
                
                if (isOutParam(method, j))
                {
                    MessagePartInfo part = outHeaders.getMessagePart(q);
                    if (part == null)
                        throw new XFireRuntimeException("Could not find header " + q + " in wsdl for operation " + op.getName());
                    
                    part.setTypeClass(paramClasses[j]);
                    part.setIndex(j);
                    part.setSchemaType(null);
                }

                if (isInParam(method, j))
                {
                    MessagePartInfo part = inHeaders.getMessagePart(q);
                    if (part == null)
                        throw new XFireRuntimeException("Could not find header " + q + " in wsdl for operation " + op.getName());
                    
                    part.setTypeClass(paramClasses[j]);
                    part.setIndex(j);
                    part.setSchemaType(null);
                }
            }
        }
    }

    /**
     * Creates a service from the specified class. The service name will be the 
     * unqualified class name. The namespace will be based on the package. 
     * The service will use soap version 1.1, wrapped style, and literal use.
     * 
     * @param clazz
     *            The service class used to populate the operations and
     *            parameters. If the class is an interface, then the
     *            implementation class that implements that interface must be
     *            set via {@link Service#setProperty(String, Object)} with the
     *            property key being
     *            {@link org.codehaus.xfire.service.invoker.ObjectInvoker#SERVICE_IMPL_CLASS}
     * @return The service.
     */
    public Service create(Class clazz)
    {
        return create(clazz, (Map) null);
    }

    /**
     * Creates a service from the specified class. The service name will be the 
     * unqualified class name. The namespace will be based on the package. 
     * The service will use soap version 1.1, wrapped style, and literal use.
     * 
     * @param clazz
     *            The service class used to populate the operations and
     *            parameters. If the class is an interface, then the
     *            implementation class that implements that interface must be
     *            set via {@link Service#setProperty(String, Object)} with the
     *            property key being
     *            {@link org.codehaus.xfire.service.invoker.ObjectInvoker#SERVICE_IMPL_CLASS}
     * @return The service.
     */
    public Service create(Class clazz, Map properties)
    {
        return create(clazz, (String) null, (String) null, properties);
    }

    protected String makeServiceNameFromClassName(Class clazz)
    {
        return ServiceUtils.makeServiceNameFromClassName(clazz);
    }

    /**
     * Creates a service from the specified class, soap version, style and use. The returned service will have a name
     * based on the class name, and a namespace based on the class package.
     * <p/>
     * Some parameters can be <code>null</code>, and will be replaced with sensible defaults if so. See the specific
     * parameters for more info.
     *
     * @param clazz            The service class used to populate the operations and parameters.
     * @param name             The name of the service. If <code>null</code>, a name will be generated from the class
     *                         name.
     * @param namespace        The default namespace of the service. If <code>null</code>, a namespace will be generated
     *                         from the class package.
     * @return The service.
     */
    public Service create(Class clazz, String name, String namespace, Map properties)
    {
        String theName = (name != null) ? name : makeServiceNameFromClassName(clazz);
        String theNamespace = (namespace != null) ? namespace : getTargetNamespace(clazz);
        QName qName = new QName(theNamespace, theName);

        String theStyle = null;
        String theUse = null;
        QName portType = null;
        Collection s11Bindings = null;
        Collection s12Bindings = null;
        String theScope="";
        
        
        if (properties != null)
        {
            theStyle = (String) properties.get(STYLE);
            theUse = (String) properties.get(USE);
            portType = (QName) properties.get(PORT_TYPE);
            s11Bindings = (List) properties.get(SOAP11_TRANSPORTS);
            s12Bindings = (List) properties.get(SOAP12_TRANSPORTS);
            theScope = (String) properties.get(SCOPE);
        }
        
        if (theStyle == null) theStyle = style;
        if (theUse == null) theUse = use;
//        if (portType == null) portType = new QName(theNamespace, theName + "PortType");
        //扩展
        if (portType == null) portType = new QName(theNamespace, clazz.getSimpleName());
        if (theScope == null) theScope = "";
        
        ServiceInfo serviceInfo = new ServiceInfo(portType, clazz);
        createDocumentationProvider(serviceInfo);
        
        
        serviceInfo.setDocumentation(getDocumentationProvider().getServiceDoc());
        
        if (theStyle.equals(SoapConstants.STYLE_WRAPPED))
            serviceInfo.setWrapped(true);
        
        Service endpoint = new Service(serviceInfo);
        endpoint.setName(qName);
        setProperties(endpoint, properties);

        final ObjectInvoker invoker = new ObjectInvoker(ScopePolicyEditor.toScopePolicy(theScope));        
        endpoint.setInvoker(invoker);
        endpoint.setFaultSerializer(new SoapFaultSerializer());

        endpoint.setWSDLWriter(new WSDLBuilderAdapter(getWsdlBuilderFactory(), endpoint, transportManager));
 
        initializeOperations(endpoint, theStyle);

        endpoint.setProperty(STYLE, theStyle);
        endpoint.setProperty(USE, theUse);
        
        boolean buildBindings = bindingCreationEnabled;
        if (properties != null && properties.containsKey(CREATE_DEFAULT_BINDINGS))
        {
            buildBindings = ((Boolean) properties.get(CREATE_DEFAULT_BINDINGS)).booleanValue();
        }
        
        if (s11Bindings == null) s11Bindings = new HashSet();
        if (s12Bindings == null) s12Bindings = new HashSet();
        
        if (buildBindings)
        {
            s11Bindings.addAll(getSoap11Transports());
            s12Bindings.addAll(getSoap12Transports());
        }
        
        createBindings(endpoint, s11Bindings, s12Bindings);
        
        try
        {
            BindingProvider provider = getBindingProvider();
            provider.initialize(endpoint);
            endpoint.setBindingProvider(provider);
        }
        catch (Exception e)
        {
            if(e instanceof XFireRuntimeException) throw (XFireRuntimeException)e;
            throw new XFireRuntimeException("Couldn't load provider.", e);
        }

        registerHandlers(endpoint);

        return endpoint;
    }

    /**
     * @param serviceInfo
     */
    protected void createDocumentationProvider(ServiceInfo serviceInfo)
    {
        XMLDocumentationBuilder docBuilder = new XMLDocumentationBuilder();
        setDocumentationProvider(EMPTY_DOC_PROVIDER);
        DocumentationProvider docProvider = docBuilder.build(serviceInfo);
        if( docProvider != null ){
            setDocumentationProvider( docProvider);    
        }
        
        
    }

    protected String getTargetNamespace(Class clazz)
    {
        return NamespaceHelper.makeNamespaceFromClassName(
                clazz.getName(), "http");
    }

    /**
     * Get a list of Transports which are enabled over SOAP 1.1.
     * @return
     */
    public Collection getSoap11Transports()
    {
        return soap11Transports;
    }
    
    public void addSoap11Transport(String id)
    {
        soap11Transports.add(id);
    }

    /**
     * Get a list of Transports which are enabled over SOAP 1.2.
     * @return
     */
    public Collection getSoap12Transports()
    {
        return soap12Transports;
    }
    
    public void addSoap12Transport(String id)
    {
        soap12Transports.add(id);
    }
    
    protected void createBindings(Service service, Collection s11, Collection s12)
    {
        for (Iterator itr = s11.iterator(); itr.hasNext();)
        {
            String bindingId = (String) itr.next();
            Transport t = transportManager.getTransport(bindingId);

            if (t instanceof SoapTransport)
            {
                createSoap11Binding(service, null, bindingId);
            }
            else if (t == null)
            {
                throw new XFireRuntimeException("Could not find binding " + bindingId );   
            }
            else
            {
                throw new XFireRuntimeException("Binding " + bindingId + " is not a SoapTransport!");            
            }
        }
        
        for (Iterator itr = s12.iterator(); itr.hasNext();)
        {
            String bindingId = (String) itr.next();
            Transport t = transportManager.getTransport(bindingId);

            if (t instanceof SoapTransport)
            {
                createSoap12Binding(service, null, bindingId);
            }
            else if (t == null)
            {
                throw new XFireRuntimeException("Could not find binding " + bindingId );   
            }
            else
            {
                throw new XFireRuntimeException("Binding " + bindingId + " is not a SoapTransport!");            
            }
        }
    }

    /**
     * Creates an endpoint for a service.  Additionally it opens a channel for this endpoint
     * as well.
     * 
     * @param service
     * @param name
     * @param url
     * @param binding
     * @return
     * @throws Exception 
     */
    public Endpoint createEndpoint(Service service, QName name, String url, Binding binding) 
        throws Exception
    {
        Endpoint endpoint = service.addEndpoint(name, binding, url);
        
        getTransportManager().getTransport(binding.getBindingId()).createChannel(url);
        
        return endpoint;
    }
    
    /**
     * Create a SOAP 1.2 binding for the specified binding id.
     * 
     * @param service
     * @param bindingName The name of the binding. If null, one will be created.
     * @param bindingId
     * @return
     */
    public Soap12Binding createSoap12Binding(Service service, QName bindingName, String bindingId)
    {
        if (bindingName == null)
        {
            SoapTransport st = (SoapTransport) transportManager.getTransport(bindingId);
            bindingName = new QName(service.getTargetNamespace(), 
                                    service.getSimpleName() + st.getName() + "12Binding");
        }
        
        Soap12Binding binding = new Soap12Binding(bindingName, bindingId, service);
        
        createSoapBinding(service, binding);
           
        return binding;
    }

    /**
    * Create a SOAP 1.1 binding for the specified binding id.
    * 
    * @param service
    * @param bindingName The name of the binding. If null, one will be created.
    * @param bindingId
    * @return
    */
    public Soap11Binding createSoap11Binding(Service service, QName bindingName, String bindingId)
    {
        if (bindingName == null)
        {
            SoapTransport st = (SoapTransport) transportManager.getTransport(bindingId);
            bindingName = new QName(service.getTargetNamespace(), 
                                    service.getSimpleName() + st.getName() + "Binding");
        }

        Soap11Binding binding = new Soap11Binding(bindingName, bindingId, service);
     
        createSoapBinding(service, binding);
        
        return binding;
    }

    protected void createSoapBinding(Service service, AbstractSoapBinding binding)
    {
        ServiceInfo serviceInfo = service.getServiceInfo();

        String style = (String) service.getProperty(STYLE);
        String use = (String) service.getProperty(USE);
        binding.setStyle(style);
        binding.setUse(use);
        binding.setSerializer(getSerializer(binding));

        // Create SOAP metadata for the binding operation
        for (Iterator itr = serviceInfo.getOperations().iterator(); itr.hasNext();)
        {
            OperationInfo op = (OperationInfo) itr.next();
            
            createBindingOperation(service, binding, op);
        }
    
        service.addBinding(binding);
        
        getBindingProvider().initialize(service, binding);
    }

    protected MessageSerializer getSerializer(AbstractSoapBinding binding)
    {
        return AbstractSoapBinding.getSerializer(binding.getStyle(),
                                                 binding.getUse());
    }
    
    protected void createBindingOperation(Service service, AbstractSoapBinding binding, OperationInfo op)
    {
        binding.setSoapAction(op, getAction(op));
        createMessageBinding(binding, op);
        /*
        for (Iterator fitr = op.getFaults().iterator(); fitr.hasNext();)
        {
            FaultInfo fault = (FaultInfo) fitr.next();
            
            // we don't support fault headers yet...
        }*/
    }

    private void createMessageBinding(AbstractSoapBinding binding, OperationInfo op)
    {
        Method method = op.getMethod();
        Class[] paramClasses = method.getParameterTypes();
        boolean isDoc = binding.getStyle().equals(SoapConstants.STYLE_DOCUMENT);
        
        MessagePartContainer inParts = binding.getHeaders(op.getInputMessage());
        MessagePartContainer outParts = null;
        if (op.hasOutput()) outParts = binding.getHeaders(op.getOutputMessage());

        for (int j = 0; j < paramClasses.length; j++)
        {
            if (!paramClasses[j].equals(MessageContext.class) && isHeader(method, j))
            {
                if (isOutParam(method, j))
                {
                    QName q = getOutParameterName(binding.getService(), op, method, j, isDoc);
                    outParts.addMessagePart(q, paramClasses[j]).setIndex(j);
                }
                
                if (isInParam(method, j))
                {
                    QName q  = getInParameterName(binding.getService(), op, method, j, isDoc);
                    inParts.addMessagePart(q, paramClasses[j]).setIndex(j);
                }
            }
        }
        
        Class returnType = method.getReturnType();
        if (isHeader(method, -1))
        {
            if (isOutParam(method, -1))
            {
                QName q = getOutParameterName(binding.getService(), op, method, -1, isDoc);
                outParts.addMessagePart(q, returnType).setIndex(-1);
            }
            
            if (isInParam(method, -1))
            {
                QName q  = getInParameterName(binding.getService(), op, method, -1, isDoc);
                inParts.addMessagePart(q, returnType).setIndex(-1);
            }
        }
    }

    protected void registerHandlers(Service service)
    {
        service.addInHandler(new ServiceInvocationHandler());
        service.addInHandler(new PostInvocationHandler());
        service.addOutHandler(new OutMessageSender());
        service.addFaultHandler(new FaultSender());
        service.addFaultHandler(new CustomFaultHandler());
    }

    private void setProperties(Service service, Map properties)
    {
        if (properties == null) return;

        for (Iterator itr = properties.entrySet().iterator(); itr.hasNext();)
        {
            Map.Entry entry = (Map.Entry) itr.next();

            service.setProperty((String) entry.getKey(), entry.getValue());
        }
    }

    protected void initializeOperations(Service endpoint, String style)
    {
        final Method[] methods = endpoint.getServiceInfo().getServiceClass().getMethods();
        Arrays.sort(methods, new MethodComparator());
        
        for (int i = 0; i < methods.length; i++)
        {
            final Method method = methods[i];

            if (isValidMethod(method))
            {
                addOperation(endpoint, method, style);
            }
        }
    }

    /**
     * Ignore the specified class' declared methods. 
     * This can be used to not expose certain interfaces as a service.
     * By default, the methods specified by the following interfaces/classes are ignored:
     * <li><code>java.lang.Object</code>
     * <li><code>org.omg.CORBA_2_3.portable.ObjectImpl</code>
     * <li><code>org.omg.CORBA.portable.ObjectImpl</code>
     * <li><code>javax.ejb.EJBObject</code>
     * <li><code>javax.ejb.EJBLocalObject</code>
     * <li><code>javax.rmi.CORBA.Stub</code>
     * 
     * @param className the fully qualified class name
     */
    public void addIgnoredMethods(String className)
    {
        ignoredClasses.add(className);
    }

    protected boolean isValidMethod(final Method method)
    {
        for (Iterator itr = serviceConfigurations.iterator(); itr.hasNext();)
        {
            ServiceConfiguration c = (ServiceConfiguration) itr.next();
            Boolean b = c.isOperation(method);
            if (b != null) return b.booleanValue();
        }
        return true;
    }    

    protected OperationInfo addOperation(Service endpoint, final Method method, String style)
    {
        ServiceInfo service = endpoint.getServiceInfo();

        final String opName = getOperationName(service, method);

        final OperationInfo op = service.addOperation(opName, method);

        final Class[] paramClasses = method.getParameterTypes();

        boolean isDoc = style.equals(SoapConstants.STYLE_DOCUMENT);

        // Setup the input message
        MessageInfo inMsg = op.createMessage(createInputMessageName(op));
        op.setInputMessage(inMsg);

        for (int j = 0; j < paramClasses.length; j++)
        {
            if (!paramClasses[j].equals(MessageContext.class) && 
                    !isHeader(method, j) &&
                    isInParam(method, j))
            {
                final QName q = getInParameterName(endpoint, op, method, j, isDoc);
                MessagePartInfo part = inMsg.addMessagePart(q, paramClasses[j]);
                part.setIndex(j);
                part.setDocumentation(getDocumentationProvider().getParamters(op, j));
                part.setSchemaElement(isDoc || endpoint.getServiceInfo().isWrapped());
            }
        }

        String mep = getMEP(method);
        op.setMEP(mep);
        if (hasOutMessage(mep))
        {
            // Setup the output message
            MessageInfo outMsg = op.createMessage(createOutputMessageName(op));
            op.setOutputMessage(outMsg);

            final Class returnType = method.getReturnType();
            if (!returnType.isAssignableFrom(void.class) && !isHeader(method, -1))
            {
                final QName q = getOutParameterName(endpoint, op, method, -1, isDoc);
                MessagePartInfo part = outMsg.addMessagePart(q, method.getReturnType());
                part.setIndex(-1);
                part.setDocumentation(getDocumentationProvider().getResultDocumentation(op));
                
                part.setSchemaElement(isDoc || endpoint.getServiceInfo().isWrapped());
            }
            
            for (int j = 0; j < paramClasses.length; j++)
            {
                if (!paramClasses[j].equals(MessageContext.class) && 
                        !isHeader(method, j) &&
                        isOutParam(method, j))
                {
                    final QName q = getInParameterName(endpoint, op, method, j, isDoc);
                    MessagePartInfo part = outMsg.addMessagePart(q, paramClasses[j]);
                    part.setIndex(j);
                    part.setSchemaElement(isDoc || endpoint.getServiceInfo().isWrapped());
                }
            }
        }

        if (isCustomFaultsEnabled())
            initializeFaults(endpoint, op);
        
        op.setAsync(isAsync(method));
        op.setDocumenation(documentationProvider.getOperationDoc(op));
        return op;
    }

    protected boolean isOutParam(Method method, int j)
    {
        for (Iterator itr = serviceConfigurations.iterator(); itr.hasNext();)
        {
            ServiceConfiguration c = (ServiceConfiguration) itr.next();
            Boolean b = c.isOutParam(method, j);
            if (b != null) return b.booleanValue();
        }
        return true;
    }

    protected boolean isInParam(Method method, int j)
    {
        for (Iterator itr = serviceConfigurations.iterator(); itr.hasNext();)
        {
            ServiceConfiguration c = (ServiceConfiguration) itr.next();
            Boolean b = c.isInParam(method, j);
            if (b != null) return b.booleanValue();
        }
        return true;
    }
    
    protected QName createInputMessageName(final OperationInfo op)
    {
        for (Iterator itr = serviceConfigurations.iterator(); itr.hasNext();)
        {
            ServiceConfiguration c = (ServiceConfiguration) itr.next();
            QName q = c.getInputMessageName(op);
            if (q != null) return q;
        }
        throw new IllegalStateException("ServiceConfiguration must provide a value!");
    }

    protected QName createOutputMessageName(final OperationInfo op)
    {
        for (Iterator itr = serviceConfigurations.iterator(); itr.hasNext();)
        {
            ServiceConfiguration c = (ServiceConfiguration) itr.next();
            QName q = c.getOutputMessageName(op);
            if (q != null) return q;
        }
        throw new IllegalStateException("ServiceConfiguration must provide a value!");
    }
    
    protected boolean hasOutMessage(String mep)
    {
        for (Iterator itr = serviceConfigurations.iterator(); itr.hasNext();)
        {
            ServiceConfiguration c = (ServiceConfiguration) itr.next();
            Boolean b = c.hasOutMessage(mep);
            if (b != null) return b.booleanValue();
        }
        return true;
    }
    
    protected void initializeFaults(final Service service, 
                                    final OperationInfo op)
    {
        // Set up the fault messages
        final Class[] exceptionClasses = op.getMethod().getExceptionTypes();
        for (int i = 0; i < exceptionClasses.length; i++)
        {
            Class exClazz = exceptionClasses[i];
            
            // Ignore XFireFaults because they don't need to be declared
            if (exClazz.equals(XFireFault.class) ||
                    exClazz.equals(Exception.class) ||
                    exClazz.equals(RuntimeException.class) ||
                    exClazz.equals(Throwable.class))
            {
                continue;
            }
            
            addFault(service, op, exClazz);
        }
    }

    protected FaultInfo addFault(final Service service, final OperationInfo op, Class exClass)
    {
        Class beanClass = exClass;
        if (isFaultInfoClass(exClass))
        {
            Method method;
            try
            {
                method = exClass.getMethod("getFaultInfo", new Class[0]);
                beanClass = method.getReturnType();
            }
            catch (SecurityException e)
            {
                throw new XFireRuntimeException("Couldn't access getFaultInfo method.", e);
            }
            catch (NoSuchMethodException e)
            {
                beanClass = exClass;
            }
        }
        
        QName name = getFaultName(service, op, exClass, beanClass);

        FaultInfo info = op.addFault(name.getLocalPart());
        info.setExceptionClass(exClass);
        info.addMessagePart(name, beanClass);
        info.setDocumentation(getDocumentationProvider().getExceptionDocumentation(op, exClass.getName()));
        
        return info;
    }

    protected boolean isFaultInfoClass(Class exClass)
    {
        return FaultInfoException.class.isAssignableFrom(exClass);
    }

    protected QName getFaultName(Service service, OperationInfo o, Class exClass, Class beanClass)
    {
        for (Iterator itr = serviceConfigurations.iterator(); itr.hasNext();)
        {
            ServiceConfiguration c = (ServiceConfiguration) itr.next();
            QName q = c.getFaultName(service, o, exClass, beanClass);
            if (q != null) return q;
        }
        throw new IllegalStateException("ServiceConfiguration must provide a value!");
    }
    
    protected String getAction(OperationInfo op)
    {
        for (Iterator itr = serviceConfigurations.iterator(); itr.hasNext();)
        {
            ServiceConfiguration c = (ServiceConfiguration) itr.next();
            String s = c.getAction(op);
            if (s != null) return s;
        }
        throw new IllegalStateException("ServiceConfiguration must provide a value!");
    }

    protected boolean isHeader(Method method, int j)
    {
        for (Iterator itr = serviceConfigurations.iterator(); itr.hasNext();)
        {
            ServiceConfiguration c = (ServiceConfiguration) itr.next();
            Boolean b = c.isHeader(method, j);
            if (b != null) return b.booleanValue();
        }
        return true;
    }

    /**
     * Creates a name for the operation from the method name. If an operation with that name
     * already exists, a name is create by appending an integer to the end. I.e. if there is already
     * two methods named <code>doSomething</code>, the first one will have an operation name of
     * "doSomething" and the second "doSomething1".
     * 
     * @param service
     * @param method
     */
    protected String getOperationName(ServiceInfo service, Method method)
    {
        for (Iterator itr = serviceConfigurations.iterator(); itr.hasNext();)
        {
            ServiceConfiguration c = (ServiceConfiguration) itr.next();
            String s = c.getOperationName(service, method);
            if (s != null) return s;
        }
        throw new IllegalStateException("ServiceConfiguration must provide a value!");
    }

    protected String getMEP(final Method method)
    {
        for (Iterator itr = serviceConfigurations.iterator(); itr.hasNext();)
        {
            ServiceConfiguration c = (ServiceConfiguration) itr.next();
            String s = c.getMEP(method);
            if (s != null) return s;
        }
        throw new IllegalStateException("ServiceConfiguration must provide a value!");
    }

    protected boolean isAsync(final Method method)
    {
        for (Iterator itr = serviceConfigurations.iterator(); itr.hasNext();)
        {
            ServiceConfiguration c = (ServiceConfiguration) itr.next();
            Boolean b = c.isAsync(method);
            if (b != null) return b.booleanValue();
        }
        return true;
    }

    protected QName getInParameterName(final Service service,
                                       final OperationInfo op,
                                       final Method method,
                                       final int paramNumber,
                                       final boolean doc)
    {if (paramNumber == -1) throw new RuntimeException();
        for (Iterator itr = serviceConfigurations.iterator(); itr.hasNext();)
        {
            ServiceConfiguration c = (ServiceConfiguration) itr.next();
            QName q = c.getInParameterName(service, op, method, paramNumber, doc);
            if (q != null) return q;
        }
        throw new IllegalStateException("ServiceConfiguration must provide a value!");
    }

    protected QName getOutParameterName(final Service service, 
                                        final OperationInfo op, 
                                        final Method method, 
                                        final int paramNumber,
                                        final boolean doc)
    {
        for (Iterator itr = serviceConfigurations.iterator(); itr.hasNext();)
        {
            ServiceConfiguration c = (ServiceConfiguration) itr.next();
            QName q = c.getOutParameterName(service, op, method, paramNumber, doc);
            if (q != null) return q;
        }
        throw new IllegalStateException("ServiceConfiguration must provide a value!");
    }

    public TransportManager getTransportManager()
    {
        return transportManager;
    }

    public void setTransportManager(TransportManager transportManager)
    {
        this.transportManager = transportManager;
    }

    public void setBindingProvider(BindingProvider bindingProvider)
    {
        this.bindingProvider = bindingProvider;
    }

    public String getStyle()
    {
        return style;
    }

    public void setStyle(String style)
    {
        this.style = style;
    }

    public String getUse()
    {
        return use;
    }

    public void setUse(String use)
    {
        this.use = use;
    }

    public boolean isVoidOneWay()
    {
        return voidOneWay;
    }

    public void setVoidOneWay(boolean voidOneWay)
    {
        this.voidOneWay = voidOneWay;
    }

    public WSDLBuilderFactory getWsdlBuilderFactory()
    {
        return wsdlBuilderFactory;
    }

    public void setWsdlBuilderFactory(WSDLBuilderFactory wsdlBuilderFactory)
    {
        this.wsdlBuilderFactory = wsdlBuilderFactory;
    }

    public boolean isCustomFaultsEnabled()
    {
        return customFaultsEnabled;
    }

    public void setCustomFaultsEnabled(boolean customFaultsEnabled)
    {
        this.customFaultsEnabled = customFaultsEnabled;
    }

    public boolean isBindingCreationEnabled()
    {
        return bindingCreationEnabled;
    }

    public void setBindingCreationEnabled(boolean bindingCreationEnabled)
    {
        this.bindingCreationEnabled = bindingCreationEnabled;
    }

    public Set getIgnoredClasses()
    {
        return ignoredClasses;
    }

    public List getServiceConfigurations()
    {
        return serviceConfigurations;
    }

    public void setServiceConfigurations(List serviceConfigurations)
    {
        this.serviceConfigurations = serviceConfigurations;
    }

    protected DocumentationProvider getDocumentationProvider()
    {
        return documentationProvider;
    }

    protected void setDocumentationProvider(DocumentationProvider documentationProvider)
    {
        this.documentationProvider = documentationProvider;
    }
    
    
    
}
