package com.dianping.dpsf.component.impl;

import com.dianping.dpsf.Constants;
import com.dianping.dpsf.component.ClusterMeta;
import com.dianping.dpsf.component.ClusterMetaParser;
import com.dianping.dpsf.exception.DPSFException;
import com.dianping.dpsf.invoke.filter.ClusterDelegateInvokeFilter;
import com.dianping.dpsf.invoke.filter.cluster.FailoverClusterInvokeFilter;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: jian.liu
 * Date: 13-1-11
 * Time: 下午2:18
 * To change this template use File | Settings | File Templates.
 */
public class DefaultClusterMetaParser implements ClusterMetaParser {

    @Override
    public ClusterMeta parse(Map<String, String> clusterConfig) {
        ClusterMeta clusterMeta = new ClusterMeta();
        if (clusterConfig != null) {
            for (Map.Entry<String, String> configEntry : clusterConfig.entrySet()) {
                if (StringUtils.isNotBlank(configEntry.getValue())) {
                    clusterMeta.addMetaItem(configEntry.getKey(), parseClusterSetting(configEntry.getValue()));
                }
            }
        }
        return clusterMeta;
    }

    private ClusterMeta.ClusterMetaItem parseClusterSetting(String clusterSetting) {
        ClusterMeta.ClusterMetaItem metaItem = new ClusterMeta.ClusterMetaItem();
        Map<String, String> entryMap = new HashMap<String, String>();
        String[] configItems = StringUtils.split(clusterSetting, ",");
        for (String configItem : configItems) {
            if (StringUtils.isNotBlank(configItem)) {
                String[] entry = StringUtils.split(configItem, ":");
                if (entry.length != 2) {
                    throw new DPSFException("Cluster config must be pattern as 'key1:value1,key2:value2'.");
                }
                entryMap.put(entry[0].trim(), entry[1].trim());
            }
        }
        if (!entryMap.isEmpty()) {
            parseClusterConfig(metaItem, entryMap);
        }
        return metaItem;
    }

    private void parseClusterConfig(ClusterMeta.ClusterMetaItem metaItem, Map<String, String> configMap) {
        String clusterProperty = Constants.CONFIG_CLUSTER_CLUSTER;
        String clusterName = configMap.get(clusterProperty);
        Set<String> supportedCluster = ClusterDelegateInvokeFilter.getSupportedCluster();
        if (clusterName == null) {
            throw new DPSFException("Cluster config must supply '" + clusterProperty + "' property, as '" + clusterProperty + ":[" + StringUtils.join(supportedCluster, ",") + "]'.");
        }
        if (!supportedCluster.contains(clusterName)) {
             throw new DPSFException("Cluster[" + clusterName + "] not supported, only support [" + StringUtils.join(supportedCluster, ",") + "].");
        }
        metaItem.setName(clusterName);
        for (Map.Entry<String, String> entry : configMap.entrySet()) {
            metaItem.setAttribute(entry.getKey(), entry.getValue());
        }
        if (FailoverClusterInvokeFilter.NAME.equals(clusterName)) {
            parseFailoverConfig(metaItem, configMap);
        }
    }

    private void parseFailoverConfig(ClusterMeta.ClusterMetaItem metaItem, Map<String, String> configMap) {
        Integer retryInt = Constants.DEFAULT_FAILOVER_RETRY;
        String retryProperty = Constants.CONFIG_CLUSTER_RETRY;
        String retry = configMap.get(retryProperty);
        if (retry != null) {
            try {
                retryInt = Integer.parseInt(retry);
            } catch (NumberFormatException e) {
                throw new DPSFException("Cluster[" + FailoverClusterInvokeFilter.NAME + "]'s " + retryProperty + " property must be integer.");
            }
            if (retryInt < 0 || retryInt > 2) {
                throw new DPSFException("Cluster[" + FailoverClusterInvokeFilter.NAME + "]'s " + retryProperty + " property must be 1 or 2.");
            }
        }
        metaItem.setAttribute(retryProperty, retryInt);

        String timeoutRetryProperty = Constants.CONFIG_CLUSTER_TIMEOUT_RETRY;
        String timeoutRetry = configMap.get(timeoutRetryProperty);
        if (timeoutRetry != null) {
            if ("0".equals(timeoutRetry)) {
                metaItem.setAttribute(timeoutRetryProperty, false);
            } else if ("1".equals(timeoutRetry)) {
                metaItem.setAttribute(timeoutRetryProperty, true);
            } else {
                throw new DPSFException("Cluster[" + FailoverClusterInvokeFilter.NAME + "]'s " + timeoutRetryProperty + " property must be 0 or 1.");
            }
        } else {
            metaItem.setAttribute(timeoutRetryProperty, Constants.DEFAULT_FAILOVER_TIMEOUT_RETRY);
        }
    }

}
