package com.hatsukoi.genesis.transport;

import com.hatsukoi.genesis.Constants;
import com.hatsukoi.genesis.codec.RpcDecoder;
import com.hatsukoi.genesis.codec.RpcEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author gaoweilin
 * @date 2022/06/23 Thu 1:14 AM
 */
public class RpcServer {
    /**
     * bossGroup表示监听接口、accept新连接的线程组
     */
    private EventLoopGroup bossGroup;
    /**
     * workerGroup表示处理每条连接读写的线程组
     */
    private EventLoopGroup workerGroup;
    /**
     * 引导类
     */
    private ServerBootstrap serverBootstrap;
    protected int port;
    private Channel channel;

    /**
     * 构建启动服务端
     * @param port
     */
    public RpcServer(int port) {
        this.port = port;
        // 创建并配置客户端Bootstrap
        serverBootstrap = new ServerBootstrap();
        // 创建boss和worker两个EventLoopGroup线程组
        bossGroup = NettyEventLoopFactory.eventLoopGroup(1, "NettyServerBoss");
        // workerGroup 是按照线程数是按照 CPU 核数计算得到的
        workerGroup = NettyEventLoopFactory.eventLoopGroup(Constants.DEFAULT_IO_THREADS, "NettyServerWorker");

        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .option(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                // LoggingHandler继承了ChannelDuplexHandler，那么无论是inbound事件还是outbound事件都会经过LoggingHandler
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("rpc-decoder", new RpcDecoder());
                        ch.pipeline().addLast("rpc-encoder", new RpcEncoder());
                        ch.pipeline().addLast("server-handler", new RpcServerHandler());
                    }
                });
    }

    public ChannelFuture start() {
        // 监听指定的端口
        ChannelFuture future = serverBootstrap.bind(this.port);
        channel = future.channel();
        channel.closeFuture();
        return future;
    }


}
