package com.hatsukoi.genesis.registry;

import com.hatsukoi.genesis.common.URL;

import java.util.List;

/**
 * 注册服务的基本行为
 * @author gaoweilin
 * @date 2022/06/03 Fri 7:24 PM
 */
public interface RegistryService {
    /**
     * 注册一个url
     * @param url
     */
    void register(URL url);

    /**
     * 取消注册一个url
     * @param url
     */
    void unregister(URL url);

    /**
     * Subscribe to eligible registered data
     * and automatically push when the registered data is changed.
     * @param url      Subscription condition
     * @param listener A listener of the change event
     */
    void subscribe(URL url, NotifyListener listener);
    /**
     * Unsubscribe to eligible registered data
     * @param url      Subscription condition
     * @param listener A listener of the change event
     */
    void unsubscribe(URL url, NotifyListener listener);

    /**
     * Query the registered data that matches the conditions
     * @param url       Query condition
     * @return          The registered information list
     */
    List<URL> lookup(URL url);
}
