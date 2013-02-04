package com.dianping.dpsf;

import com.dianping.dpsf.PigeonBootStrap.Container;

/**
 * 
 * Container Aware interface, in order to inject container instance
 * @author jian.liu
 *
 */
public interface ContainerAware {
    
    void setContainer(Container container);

}
