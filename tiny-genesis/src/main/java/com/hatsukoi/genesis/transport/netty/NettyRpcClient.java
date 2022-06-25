package com.hatsukoi.genesis.transport.netty;

import com.hatsukoi.genesis.Constants;
import com.hatsukoi.genesis.codec.RpcDecoder;
import com.hatsukoi.genesis.codec.RpcEncoder;
import com.hatsukoi.genesis.transport.RpcClientHandler;
import com.hatsukoi.genesis.transport.netty.NettyEventLoopFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author gaoweilin
 * @date 2022/06/23 Thu 1:14 AM
 */
public class NettyRpcClient implements Closeable {

    protected Bootstrap clientBootstrap;
    protected EventLoopGroup workerGroup;
    private String host;
    private int port;

    /**
     * 构建启动客户端
     * @param host
     * @param port
     */
    public NettyRpcClient(String host, int port) {
        this.host = host;
        this.port = port;
        // 创建并配置客户端Bootstrap
        clientBootstrap = new Bootstrap();
        // 指定线程模型
        workerGroup = NettyEventLoopFactory.eventLoopGroup(Constants.DEFAULT_IO_THREADS, "NettyClientWorker");
        clientBootstrap.group(workerGroup)
                .channel(NioSocketChannel.class) // 指定IO类型为NIO
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception { // TODO: 顺序有问题
                        ch.pipeline().addLast("rpc-encoder", new RpcEncoder());
                        ch.pipeline().addLast("rpc-decoder", new RpcDecoder());
                        ch.pipeline().addLast("client-handler", new RpcClientHandler());
                    }
                });
    }

    /**
     * 客户端连接指定的地址和端口
     * @return
     */
    public ChannelFuture connect() {
        ChannelFuture connect = clientBootstrap.connect(host, port);
        connect.awaitUninterruptibly();
        return connect;
    }

    @Override
    public void close() throws IOException {
        workerGroup.shutdownGracefully();
    }
}
