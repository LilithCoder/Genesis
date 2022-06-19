package com.hatsukoi.genesis.registry;

import com.hatsukoi.genesis.common.URL;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 实现了 RegistryFactory 接口的抽象类
 * 提供了缓存 Registry 对象的功能
 * 并未真正实现 Registry 的创建，具体的创建逻辑是由子类完成
 * @author gaoweilin
 * @date 2022/06/05 Sun 10:17 AM
 */
public abstract class AbstractRegistryFactory implements RegistryFactory{
    private static final Logger logger = Logger.getLogger(AbstractRegistryFactory.class);
    /**
     * 所有注册中心节点是否都已经被摧毁
     */
    private static final AtomicBoolean destroyed = new AtomicBoolean(false);
    /**
     * key为注册中心节点地址，value为的注册中心节点
     */
    protected static final Map<String, Registry> REGISTRIES = new HashMap<>();

    /**
     * 获取注册中心节点 (缓存or新建)
     * 提供了缓存 Registry 对象的功能，并未真正实现 Registry 的创建，具体的创建逻辑是由子类完成的
     * @param url
     * @return
     */
    @Override
    public Registry getRegistry(URL url) {
        if (destroyed.get()) {
            logger.info("All register node instance has been destroyed");
            return null;
        }
        // 规范 URL：将 RegistryService 的类名设置为 URL path 和 interface 参数
        url = url.setPath(RegistryService.class.getName())
                .addParam("interface", RegistryService.class.getName());
        String key = url.toString();

        // 先看缓存里有没有注册中心节点实例，没有就新建并缓存
        synchronized (REGISTRIES) {
            Registry registry;
            if ((registry = REGISTRIES.get(key)) != null) {
                return registry;
            }
            Registry newRegistry = createRegistry(url);
            if (newRegistry != null) {
                REGISTRIES.put(key, newRegistry);
                return newRegistry;
            } else {
                String errMsg = "Cannot create Registry Node";
                logger.error(errMsg);
                throw new IllegalStateException(errMsg);
            }
        }
    }

    /**
     * 新建一个注册中心节点
     * @param url
     * @return
     */
    protected abstract Registry createRegistry(URL url);
}
