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

import com.dianping.dpsf.invoke.filter.cluster.FailfastClusterInvokeFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO Comment of The Class
 *
 * @author danson.liu
 */
public class ClusterMeta {

    private Map<String, ClusterMetaItem>        clusterMetaItems            =        new HashMap<String, ClusterMetaItem>();
    private static final ClusterMetaItem      DEFAULT_CLUSTER_ITEM       =        new ClusterMetaItem(FailfastClusterInvokeFilter.NAME);

    public void addMetaItem(String methodPattern, ClusterMetaItem metaItem) {
        clusterMetaItems.put(methodPattern, metaItem);
    }

    public ClusterMetaItem matchCluster(String method) {
        ClusterMetaItem defaultMetaItem = DEFAULT_CLUSTER_ITEM;
        for (Map.Entry<String, ClusterMetaItem> entry : clusterMetaItems.entrySet()) {
            String methodPattern = entry.getKey();
            if (methodPattern.equals(method) || methodPattern.contains("|" + method + "|") || methodPattern.startsWith(method + "|")
                    || methodPattern.endsWith("|" + method)) {
                return entry.getValue();
            }
            if ("*".equals(methodPattern)) {
                defaultMetaItem = entry.getValue();
            }
        }
        return defaultMetaItem;
    }

    public static class ClusterMetaItem {

        private String name;

        private Map<String, Object> attributes;

        ClusterMetaItem(String name) {
            this.name = name;
        }

        public ClusterMetaItem() {
        }

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

        public void setAttribute(String name, Object value) {
            if (this.attributes == null) {
                this.attributes = new HashMap<String, Object>();
            }
            this.attributes.put(name, value);
        }

        public Object getAttribute(String name) {
            if (this.attributes == null) {
                return null;
            }
            return this.attributes.get(name);
        }

    }

}
