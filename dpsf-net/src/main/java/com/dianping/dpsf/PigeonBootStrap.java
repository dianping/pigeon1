/**
 * File Created at 12-12-31
 *
 * Copyright 2010 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dianping.dpsf;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.util.internal.ConcurrentHashMap;

import com.dianping.dpsf.component.Invoker;
import com.dianping.dpsf.component.impl.DefaultInvoker;
import com.dianping.dpsf.exception.DPSFException;
import com.dianping.dpsf.invoke.RemoteInvocationHandlerFactory;
import com.dianping.dpsf.invoke.RemoteInvocationRepository;
import com.dianping.dpsf.invoke.RemoteInvocationRepositoryImpl;
import com.dianping.dpsf.invoke.filter.ClusterDelegateInvokeFilter;
import com.dianping.dpsf.invoke.filter.ContextPrepareInvokeFilter;
import com.dianping.dpsf.invoke.filter.GatewayInvokeFilter;
import com.dianping.dpsf.invoke.filter.MockInvokeFilter;
import com.dianping.dpsf.invoke.filter.PerformanceInvokeFilter;
import com.dianping.dpsf.invoke.filter.RemoteCallInvokeFilter;
import com.dianping.dpsf.invoke.filter.RemoteCallMonitorInvokeFilter;
import com.dianping.dpsf.invoke.filter.RemoteCallStatInvokeFilter;
import com.dianping.dpsf.invoke.filter.ServiceCallMonitorInvokeFilter;
import com.dianping.dpsf.invoke.filter.cluster.FailfastClusterInvokeFilter;
import com.dianping.dpsf.invoke.filter.cluster.FailoverClusterInvokeFilter;
import com.dianping.dpsf.net.channel.manager.ClientManager;
import com.dianping.dpsf.net.channel.netty.NettyClientManager;
import com.dianping.dpsf.process.filter.BusinessProcessFilter;
import com.dianping.dpsf.process.filter.ContextTransferProcessFilter;
import com.dianping.dpsf.process.filter.EchoProcessFilter;
import com.dianping.dpsf.process.filter.ExceptionProcessFilter;
import com.dianping.dpsf.process.filter.HeartbeatProcessFilter;
import com.dianping.dpsf.process.filter.MonitorProcessFilter;
import com.dianping.dpsf.process.filter.PerformanceProcessFilter;
import com.dianping.dpsf.process.filter.WriteResponseProcessFilter;
import com.dianping.dpsf.spi.InvocationInvokeFilter.InvokePhase;
import com.dianping.dpsf.spi.InvocationProcessFilter.ProcessPhase;

/**
 * Bootstrap Pigeon to setup
 * 1. 构建系统全局的组件单元
 * 2. 加载插件中的Filter, Listener组件，并进行组装
 *
 * @author danson.liu
 */
public class PigeonBootStrap {

    private static volatile boolean     isClientSetup           = false;
    private static volatile boolean     isClientSetupEnabled    = true;
    private static volatile boolean     isServerSetup           = false;
    private static volatile boolean     isServerSetupEnabled    = true;
    
    private static Container            container               = new ContainerImpl();

    public static void setupClient() {
        if (isClientSetupEnabled && !isClientSetup) {
            synchronized (PigeonBootStrap.class) {
                if (!isClientSetup) {
                    setupBasicClientComponents();
                    setupInvocationInvokeFilters();
                    setupOtherClientComponents();
                    isClientSetup = true;
                }
            }
        }
    }

    private static void setupBasicClientComponents() {
        RemoteInvocationRepository invocationRepository = new RemoteInvocationRepositoryImpl();
        container.registerComponent(invocationRepository);
        ClientManager clientManager = new NettyClientManager(invocationRepository);
        container.registerComponent(clientManager);
        Invoker invoker = new DefaultInvoker(clientManager);     //老的逻辑，使用新逻辑后可删除
        container.registerComponent(invoker);
    }

    private static void setupInvocationInvokeFilters() {
        ClientManager clientManager = container.getComponentByType(ClientManager.class);
        RemoteInvocationRepository invocationRepository = container.getComponentByType(RemoteInvocationRepository.class);
        
        RemoteInvocationHandlerFactory.registerInternalInvokeFilter(InvokePhase.Finalize, new GatewayInvokeFilter());
        
        RemoteInvocationHandlerFactory.registerInternalInvokeFilter(InvokePhase.Finalize, new PerformanceInvokeFilter());
        
        RemoteInvocationHandlerFactory.registerInternalInvokeFilter(InvokePhase.Error_Handle, new MockInvokeFilter());
        RemoteInvocationHandlerFactory.registerInternalInvokeFilter(InvokePhase.Before_Cluster, new ServiceCallMonitorInvokeFilter());
        RemoteInvocationHandlerFactory.registerInternalInvokeFilter(InvokePhase.Cluster, new ClusterDelegateInvokeFilter());
        
        RemoteInvocationHandlerFactory.registerInternalInvokeFilter(InvokePhase.Before_Call, new RemoteCallMonitorInvokeFilter());
        RemoteInvocationHandlerFactory.registerInternalInvokeFilter(InvokePhase.Before_Call, new RemoteCallStatInvokeFilter());
        
        RemoteInvocationHandlerFactory.registerInternalInvokeFilter(InvokePhase.Call, new ContextPrepareInvokeFilter());
        RemoteInvocationHandlerFactory.registerInternalInvokeFilter(InvokePhase.Call, new RemoteCallInvokeFilter(invocationRepository));

        ClusterDelegateInvokeFilter.registerCluster(new FailfastClusterInvokeFilter(clientManager));
        ClusterDelegateInvokeFilter.registerCluster(new FailoverClusterInvokeFilter(clientManager));
    }

    private static void setupOtherClientComponents() {
//        RemoteInvocationRepository.INSTANCE.init();
    }

    public static void setupServer() {
        if (isServerSetupEnabled && !isServerSetup) {
            synchronized (PigeonBootStrap.class) {
                if (!isServerSetup) {
                    setupInvocationProcessFilters();
                    isServerSetup = true;
                }
            }
        }
    }

    private static void setupInvocationProcessFilters() {
        RemoteInvocationHandlerFactory.registerInternalProcessFilter(ProcessPhase.Before_Write, new MonitorProcessFilter());
//        RemoteInvocationHandlerFactory.registerInternalProcessFilter(ProcessPhase.Write, new WriteResponseProcessFilter());
        RemoteInvocationHandlerFactory.registerInternalProcessFilter(ProcessPhase.Before_Execute, new HeartbeatProcessFilter());
        RemoteInvocationHandlerFactory.registerInternalProcessFilter(ProcessPhase.Before_Execute, new ContextTransferProcessFilter());
        RemoteInvocationHandlerFactory.registerInternalProcessFilter(ProcessPhase.Before_Execute, new ExceptionProcessFilter());
        RemoteInvocationHandlerFactory.registerInternalProcessFilter(ProcessPhase.Execute, new EchoProcessFilter());
        RemoteInvocationHandlerFactory.registerInternalProcessFilter(ProcessPhase.Execute, new PerformanceProcessFilter());
        RemoteInvocationHandlerFactory.registerInternalProcessFilter(ProcessPhase.Execute, new BusinessProcessFilter());
    }
    
    public static void shutdown() {
        if (container != null) {
            isClientSetup = false;
            isServerSetup = false;
            RemoteInvocationHandlerFactory.clearInternalFilters();
            container.shutdown();
        }
    }
    
    public static Container getContainer() {
        return container;
    }
    
    public static void setClientSetupEnabled(boolean enabled) {
        isClientSetupEnabled = enabled;
    }
    
    public static void setServerSetupEnabled(boolean enabled) {
        isServerSetupEnabled = enabled;
    }

    public static interface Container {
        <T> T   getComponentByType(Class<T> type);
        <T> T   getRequiredComponentByType(Class<T> type);
        <T> T   getComponentByName(String name);
        <T> T   getRequiredComponentByName(String name);
        void    registerComponent(Object component);
        void    registerComponent(String name, Object component);
        void    shutdown();
    }
    
    public static class ContainerImpl implements Container {
        private Map<String, Object> components      = new ConcurrentHashMap<String, Object>();
        private AtomicLong          nameGenerator   = new AtomicLong();

        @SuppressWarnings("unchecked")
        @Override
        public <T> T getComponentByName(String name) {
            return (T) components.get(name);
        }
        
        public <T> T getRequiredComponentByName(String name) {
            T component = (T)getComponentByName(name);
            if (component == null) {
                throw new IllegalStateException("Component[name=" + name + "] is required, but not found.");
            }
            return component;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T getComponentByType(Class<T> type) {
            Object component = null;
            for (Entry<String, Object> entry : components.entrySet()) {
                if (type.isInstance(entry.getValue())) {
                    if (component != null) {
                        throw new IllegalStateException("Component[type=" + type.getName() + "] was expect only one, but found more than it.");
                    }
                    component = entry.getValue();
                }
            }
            return (T) component;
        }
        
        public <T> T getRequiredComponentByType(Class<T> type) {
            T component = getComponentByType(type);
            if (component == null) {
                throw new IllegalStateException("Component[type=" + type.getName() + "] is required, but not found.");
            }
            return component;
        }

        @Override
        public void registerComponent(Object component) {
            if (component == null) {
                throw new NullPointerException("component cannot be null.");
            }
            String name = component.getClass().getName() + "#" + nameGenerator.incrementAndGet();
            registerComponent(name, component);
        }

        @Override
        public void registerComponent(String name, Object component) {
            if (components.containsKey(name)) {
                throw new IllegalStateException("Component[name=" + name + ", instance=" + components.get(name) + "] already exists.");
            }
            if (component instanceof ContainerAware) {
                ((ContainerAware) component).setContainer(this);
            }
            if (component instanceof Initializable) {
                try {
                    ((Initializable) component).init();
                } catch (Exception e) {
                    throw new DPSFException("Initialize component[name=" + name + ", instance=" + component + "] failed.", e);
                }
            }
            components.put(name, component);
        }

        @Override
        public void shutdown() {
            for (Entry<String, Object> entry : components.entrySet()) {
                if (entry.getValue() instanceof Disposable) {
                    try {
                        ((Disposable) entry.getValue()).destroy();
                    } catch (Exception e) {
                        throw new DPSFException("Shutdown container failed with destroy component[name=" + entry.getKey() 
                                + ", instance=" + entry.getValue() + "] error.", e);
                    }
                }
            }
            components.clear();
        }
        
    }

}
