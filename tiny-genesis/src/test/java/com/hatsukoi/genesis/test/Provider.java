package com.hatsukoi.genesis.test;

import com.hatsukoi.genesis.factory.BeanManager;
import com.hatsukoi.genesis.registry.ServerInfo;
import com.hatsukoi.genesis.registry.ZookeeperRegistry;
import com.hatsukoi.genesis.transport.RpcServer;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.log4j.Logger;

import java.util.Scanner;

/**
 * @author gaoweilin
 * @date 2022/06/24 Fri 2:54 AM
 */
public class Provider {
    private static final Logger logger = Logger.getLogger(Provider.class);
    public static void main(String[] args) throws Throwable {
        // 创建DemoServiceImpl，并注册到BeanManager中
        BeanManager.registerBean("demoService", new DemoServiceImpl());
        // 创建ZookeeperRegistry，并将Provider的地址信息封装成ServerInfo，注册到Zookeeper
        ZookeeperRegistry<ServerInfo> discovery = new ZookeeperRegistry<>();
        discovery.start();
        ServerInfo serverInfo = new ServerInfo("127.0.0.1", 20800);
        discovery.registerService(ServiceInstance.<ServerInfo>builder().name("demoService").payload(serverInfo).build());
        // 启动DemoRpcServer，等待Client的请求
        RpcServer rpcServer = new RpcServer(20000);
        rpcServer.start();
        logger.info("Provider Service has already started...");
        new Scanner(System.in);
    }
}
