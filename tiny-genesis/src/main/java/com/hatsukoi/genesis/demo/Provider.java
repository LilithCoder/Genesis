package com.hatsukoi.genesis.demo;

import com.hatsukoi.genesis.annotation.RpcScan;
import com.hatsukoi.genesis.factory.BeanFactory;
import com.hatsukoi.genesis.transport.netty.NettyRpcServer;
import org.apache.log4j.Logger;

/**
 * 服务提供者main入口
 * @author gaoweilin
 * @date 2022/06/24 Fri 2:54 AM
 */
@RpcScan(basePackage = "com.hatsukoi.genesis")
public class Provider {
    private static final Logger logger = Logger.getLogger(Provider.class);
//    public static void main(String[] args) throws Throwable {
//        // 创建DemoServiceImpl，并注册到BeanManager中
//        BeanManager.registerBean("demoService", new DemoServiceImpl());
//        // 创建ZookeeperRegistry，并将Provider的地址信息封装成ServerInfo，注册到Zookeeper
//        ZookeeperRegistry<ServerInfo> discovery = new ZookeeperRegistry<>();
//        discovery.start();
//        ServerInfo serverInfo = new ServerInfo("127.0.0.1", 20800);
//        discovery.registerService(ServiceInstance.<ServerInfo>builder().name("demoService").payload(serverInfo).build());
//        // 启动DemoRpcServer，等待Client的请求
//        NettyRpcServer rpcServer = new NettyRpcServer(20000);
//        rpcServer.start();
//        logger.info("Provider Service has already started...");
//        System.in.read();
//    }
    public static void main(String[] args) {
        NettyRpcServer bean = BeanFactory.getBean(NettyRpcServer.class);
    }
}
