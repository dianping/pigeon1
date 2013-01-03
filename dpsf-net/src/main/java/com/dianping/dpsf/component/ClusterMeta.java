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
package com.dianping.dpsf.component;

import com.dianping.dpsf.invoke.cluster.FailfastClusterInvocationFilter;

import java.util.Map;

/**
 * TODO Comment of The Class
 *
 * @author danson.liu
 */
public class ClusterMeta {

    private String name = FailfastClusterInvocationFilter.NAME;

    private Map<String, Object> attributes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
