package com.hatsukoi.genesis.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 业务Bean管理器
 * @author gaoweilin
 * @date 2022/06/22 Wed 12:32 AM
 */
public class BeanManager {
    /**
     * 业务Bean实例缓存
     * key   为服务名
     * value 为bean实例
     */
    private static Map<String, Object> services = new ConcurrentHashMap<>();

    public static void registerBean(String serviceName, Object bean) {
        services.put(serviceName, bean);
    }

    public static Object getBean(String serviceName) {
        return services.get(serviceName);
    }
}
