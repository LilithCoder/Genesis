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
     * 订阅一个 URL
     * @param url
     * @param listener      订阅数据的监听器
     */
    void subscribe(URL url, NotifyListener listener);
    /**
     * 取消订阅一个 URL
     * @param url
     * @param listener      订阅数据的监听器
     */
    void unsubscribe(URL url, NotifyListener listener);
    /**
     * 查询符合条件的注册数据
     * @param url
     * @return
     */
    List<URL> lookup(URL url);
}
