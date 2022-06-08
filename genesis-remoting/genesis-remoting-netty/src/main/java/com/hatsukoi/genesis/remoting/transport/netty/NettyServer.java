package com.hatsukoi.genesis.remoting.transport.netty;

import com.hatsukoi.genesis.common.URL;
import com.hatsukoi.genesis.remoting.ChannelHandler;
import com.hatsukoi.genesis.remoting.transport.AbstractServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.log4j.Logger;

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
        bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {

                    }
                })
    }
}
