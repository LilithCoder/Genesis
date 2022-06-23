package com.hatsukoi.genesis.registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.InstanceSerializer;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gaoweilin
 * @date 2022/06/24 Fri 1:37 AM
 */
public class ZookeeperRegistry<T> implements Registry<T> {
    private static final String root = "/genesis/rpc";
    private static final String address = "localhost:2181";
    /**
     * JSON序列化ProviderService
     */
    private InstanceSerializer serializer = new JsonInstanceSerializer<>(ServerInfo.class);
    /**
     * 服务发现
     */
    private ServiceDiscovery<T> serviceDiscovery;
    /**
     * ServiceInstance 实例的缓存
     */
    private ServiceCache<T> serviceCache;

    public void start() throws Throwable {
        // 初始化CuratorFramework
        CuratorFramework client = CuratorFrameworkFactory.newClient(address, new ExponentialBackoffRetry(1000, 3));
        // 启动Curator客户端
        client.start();
        // 初始化ServiceDiscovery
        serviceDiscovery = ServiceDiscoveryBuilder.builder(ServerInfo.class)
                .client(client) // 依赖Curator客户端
                .basePath(root) // 管理的zk路径
                .serializer(serializer)
                .build();
        // 创建ServiceCache，监Zookeeper相应节点的变化
        serviceCache = serviceDiscovery.serviceCacheBuilder()
                .name("/demoService")
                .build();
        // 阻塞当前线程，等待连接成功
        client.blockUntilConnected();
        // 启动ServiceDiscovery
        serviceDiscovery.start();
        // 启动ServiceCache
        serviceCache.start();
    }

    @Override
    public void registerService(ServiceInstance<T> service) throws Exception {
        serviceDiscovery.registerService(service);
    }

    @Override
    public void unregisterService(ServiceInstance<T> service) throws Exception {
        serviceDiscovery.unregisterService(service);
    }

    /**
     * 根据name进行过滤ServiceCache中的缓存数据
     * @param name
     * @return
     * @throws Exception
     */
    @Override
    public List<ServiceInstance<T>> queryForInstances(String name) throws Exception {
        return serviceCache.getInstances().stream().filter(serviceInstance -> {
            return serviceInstance.getName().equals(name);
        }).collect(Collectors.toList());
    }
}
