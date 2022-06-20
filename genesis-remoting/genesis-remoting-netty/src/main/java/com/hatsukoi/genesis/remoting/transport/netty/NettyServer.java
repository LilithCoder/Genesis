package com.hatsukoi.genesis.remoting.transport.netty;

import com.hatsukoi.genesis.common.URL;
import com.hatsukoi.genesis.remoting.ChannelHandler;
import com.hatsukoi.genesis.remoting.transport.AbstractServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.log4j.Logger;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author gaoweilin
 * @date 2022/06/01 Wed 5:19 AM
 */
public class NettyServer extends AbstractServer {

    private static final Logger logger = Logger.getLogger(NettyServer.class);
    /**
     * netty server bootstrap
     */
    private ServerBootstrap bootstrap;
    /**
     * 连接监听线程组
     */
    private NioEventLoopGroup bossGroup;
    /**
     * 读写处理线程组
     */
    private NioEventLoopGroup workerGroup;


    public NettyServer(URL url, ChannelHandler handler) {
        super(url, handler);
    }

    /**
     * Init and start netty server
     * @throws Throwable
     */
    @Override
    protected void doOpen() throws Throwable {
        // 创建ServerBootstrap
        bootstrap = new ServerBootstrap();

        // 创建boss EventLoopGroup
        bossGroup = NettyEventLoopFactory.eventLoopGroup(1, "NettyServerBoss");

        // 创建worker EventLoopGroup
        workerGroup = NettyEventLoopFactory.eventLoopGroup(
                getUrl().getPositiveParameter(IO_THREADS_KEY, Constants.DEFAULT_IO_THREADS),
                "NettyServerWorker");

        // 创建NettyServerHandler，它是一个Netty中的ChannelHandler实现，
        final NettyServerHandler nettyServerHandler = new NettyServerHandler(getUrl(), this);

        // 获取当前NettyServer创建的所有Channel
        channels = nettyServerHandler.getChannels();

        bootstrap.group(bossGroup, workerGroup)
                .channel(NettyEventLoopFactory.serverSocketChannelClass())
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // 连接空闲超时时间
                        int idleTimeout = UrlUtils.getIdleTimeout(getUrl());

                        // NettyCodecAdapter中会创建Decoder和Encoder
                        NettyCodecAdapter adapter = new NettyCodecAdapter(getCodec(), getUrl(), NettyServer.this);
                        if (getUrl().getParameter(SSL_ENABLED_KEY, false)) {
                            ch.pipeline().addLast("negotiation",
                                    SslHandlerInitializer.sslServerHandler(getUrl(), nettyServerHandler));
                        }
                        ch.pipeline()
                                .addLast("decoder", adapter.getDecoder())
                                .addLast("encoder", adapter.getEncoder())
                                .addLast("server-idle-handler", new IdleStateHandler(0, 0, idleTimeout, MILLISECONDS))
                                .addLast("handler", nettyServerHandler);
                    }
                });
        // bind
        ChannelFuture channelFuture = bootstrap.bind(getBindAddress());
        channelFuture.syncUninterruptibly();
        channel = channelFuture.channel();
    }
}
