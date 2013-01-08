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
package com.dianping.dpsf.spring;

import com.dianping.dpsf.invoke.*;
import com.dianping.dpsf.invoke.filter.cluster.FailfastClusterInvokeFilter;
import com.dianping.dpsf.invoke.filter.*;
import com.dianping.dpsf.net.channel.manager.ClientManager;
import com.dianping.dpsf.net.channel.manager.ClientManagerFactory;
import com.dianping.dpsf.process.filter.*;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO Comment of The Class
 *
 * @author danson.liu
 */
public class PigeonBootStrap {

    private static volatile boolean isClientSetup = false;
    private static volatile boolean isServerSetup = false;

    private static Map<String, Object> components = new HashMap<String, Object>();

    public static void setupClient() {
        if (!isClientSetup) {
            synchronized (PigeonBootStrap.class) {
                if (!isClientSetup) {
                    setupInvocationInvokeFilters();
                    setupOtherClientComponents();
                    isClientSetup = true;
                }
            }
        }
    }

    private static void setupOtherClientComponents() {
        RemoteInvocationRepository.INSTANCE.init();
    }

    private static void setupInvocationInvokeFilters() {
        RemoteInvocationHandlerFactory.registerInternalInvokeFilter(new FinalizeInvokeFilter(10));
        RemoteInvocationHandlerFactory.registerInternalInvokeFilter(new MockInvokeFilter(20));
        RemoteInvocationHandlerFactory.registerInternalInvokeFilter(new ServiceCallMonitorInvokeFilter(30));
        RemoteInvocationHandlerFactory.registerInternalInvokeFilter(new ClusterDelegateInvokeFilter(40));
        RemoteInvocationHandlerFactory.registerInternalInvokeFilter(new ContextPrepareInvokeFilter(50));
        RemoteInvocationHandlerFactory.registerInternalInvokeFilter(new RemoteCallMonitorInvokeFilter(60));
        RemoteInvocationHandlerFactory.registerInternalInvokeFilter(new RemoteCallStatInvokeFilter(70));
        RemoteInvocationHandlerFactory.registerInternalInvokeFilter(new RemoteCallInvokeFilter(80));

        ClientManager clientManager = ClientManagerFactory.getClientManager();
        ClusterDelegateInvokeFilter.registerCluster(new FailfastClusterInvokeFilter(clientManager));
    }

    public static void setupServer() {
        if (!isServerSetup) {
            synchronized (PigeonBootStrap.class) {
                if (!isServerSetup) {
                    setupInvocationProcessFilters();
                    isServerSetup = true;
                }
            }
        }
    }

    private static void setupInvocationProcessFilters() {
        RemoteInvocationHandlerFactory.registerInternalProcessFilter(new MonitorProcessFilter(10));
        RemoteInvocationHandlerFactory.registerInternalProcessFilter(new WriteResponseProcessFilter(20));
        RemoteInvocationHandlerFactory.registerInternalProcessFilter(new ContextTransferProcessFilter(30));
        RemoteInvocationHandlerFactory.registerInternalProcessFilter(new ExceptionProcessFilter(40));
        RemoteInvocationHandlerFactory.registerInternalProcessFilter(new EchoProcessFilter(50));
        RemoteInvocationHandlerFactory.registerInternalProcessFilter(new HeartbeatProcessFilter(60));
        RemoteInvocationHandlerFactory.registerInternalProcessFilter(new BusinessProcessFilter(70));
    }

    public static <T> T getComponent(String name) {
        return (T) components.get(name);
    }

    public static void registerComponent(String name, Object component) {
        if (components.containsKey(name)) {
            throw new IllegalStateException("Component[" + name + "] already exists.");
        }
        components.put(name, component);
    }

}
