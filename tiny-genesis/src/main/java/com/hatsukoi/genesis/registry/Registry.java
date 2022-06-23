package com.hatsukoi.genesis.registry;

import org.apache.curator.x.discovery.ServiceInstance;

import java.io.IOException;
import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/06/24 Fri 1:34 AM
 */
public interface Registry<T> {
    void registerService(ServiceInstance<T> service) throws Exception;

    void unregisterService(ServiceInstance<T> service) throws Exception;

    List<ServiceInstance<T>> queryForInstances(String name) throws Exception;
}
