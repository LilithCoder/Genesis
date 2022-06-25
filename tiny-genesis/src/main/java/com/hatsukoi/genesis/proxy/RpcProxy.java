package com.hatsukoi.genesis.proxy;

import com.hatsukoi.genesis.Constants;
import com.hatsukoi.genesis.protocol.Header;
import com.hatsukoi.genesis.protocol.Message;
import com.hatsukoi.genesis.protocol.Request;
import com.hatsukoi.genesis.registry.Registry;
import com.hatsukoi.genesis.registry.ServerInfo;
import com.hatsukoi.genesis.transport.Connection;
import com.hatsukoi.genesis.transport.netty.NettyResponseFuture;
import com.hatsukoi.genesis.transport.netty.NettyRpcClient;
import io.netty.channel.ChannelFuture;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 客户端代理
 * 屏蔽底层的网络操作以及与注册中心之间的交互
 * @author gaoweilin
 * @date 2022/06/24 Fri 2:13 AM
 */
public class RpcProxy implements InvocationHandler {
    private static final Logger logger = Logger.getLogger(RpcProxy.class);
    /**
     * 需要代理的服务(接口)名称
     */
    private String serviceName;
    /**
     * Zookeeperke客户端
     */
    private Registry<ServerInfo> registry;

    public RpcProxy(String serviceName, Registry<ServerInfo> registry) {
        this.serviceName = serviceName;
        this.registry = registry;
    }

    /**
     * 生成代理对象
     * @param clazz
     * @param registry
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T newInstance(Class<T> clazz, Registry<ServerInfo> registry) throws Exception {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{clazz},
                new RpcProxy("demoService", registry));
    }

    /**
     * 当调用目标对象的时候，会执行 invoke() 方法中的代理逻辑
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 从Zookeeper缓存中获取可用的Server地址,并随机从中选择一个
        List<ServiceInstance<ServerInfo>> serviceInstances = registry.queryForInstances(serviceName);
        ServiceInstance<ServerInfo> serviceInstance = serviceInstances.get(ThreadLocalRandom.current()
                .nextInt(serviceInstances.size()));
        // 创建请求消息，然后调用remoteCall()方法请求上面选定的Server端
        String methodName = method.getName();
        Header header = new Header(Constants.MAGIC, (byte) 1);
        Message<Request> message = new Message<>(header, new Request(serviceName, methodName, args));
        return remoteCall(serviceInstance.getPayload(), message);
    }

    /**
     * 实际远程调用
     */
    private Object remoteCall(ServerInfo serverInfo, Message message) {
        if (serverInfo == null) {
            String err = "Cannot find available Provider Server";
            logger.error(err);
            throw new RuntimeException(err);
        }
        Object result = null;
        try {
            // 创建RpcClient，连接指定的Server端
            NettyRpcClient client = new NettyRpcClient(serverInfo.getHost(), serverInfo.getPort());
            ChannelFuture channelFuture = client.connect().awaitUninterruptibly();
            // 创建对应的Connection对象，并发送请求
            Connection connection = new Connection(channelFuture);
            NettyResponseFuture responseFuture = connection.request(message, Constants.DEFAULT_TIMEOUT);
            // 等待请求对应的响应
            result = responseFuture.getPromise().get(Constants.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (Throwable t) {
            String err = "Failed to make a remote procedure call";
            logger.error(err, t);
        }
        return result;
    }
}
