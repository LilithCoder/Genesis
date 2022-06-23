package com.hatsukoi.genesis.test;

import com.hatsukoi.genesis.proxy.RpcProxy;
import com.hatsukoi.genesis.registry.ServerInfo;
import com.hatsukoi.genesis.registry.ZookeeperRegistry;
import org.apache.log4j.Logger;

/**
 * @author gaoweilin
 * @date 2022/06/24 Fri 2:54 AM
 */
public class Consumer {
    private static final Logger logger = Logger.getLogger(Consumer.class);
    public static void main(String[] args) throws Throwable {
        // 创建ZookeeperRegistr对象
        ZookeeperRegistry<ServerInfo> discovery = new ZookeeperRegistry<>();
        discovery.start();
        // 创建代理对象，通过代理调用远端Server
        DemoService demoService = RpcProxy.newInstance(DemoService.class, discovery);
        // 调用sayHello()方法，并输出结果
        String result = demoService.hello("hello");
        logger.info("Get the response from Provider: " + result);
    }
}
