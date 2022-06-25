package com.hatsukoi.genesis.registry;

/**
 * @author gaoweilin
 * @date 2022/06/26 Sun 5:48 AM
 */
public interface ServiceProvider {
    public <T> void add(String serviceName, T service);
    public Object get(String serviceName);
}
