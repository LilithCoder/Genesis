package com.hatsukoi.genesis.transport;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.ThreadFactory;

/**
 * @author gaoweilin
 * @date 2022/06/23 Thu 1:21 AM
 */
public class NettyEventLoopFactory {

    /**
     * 创建EventLoopGroup
     * 对于 Linux 系统，会使用 EpollEventLoopGroup，其他系统则使用 NioEventLoopGroup
     * @param threads
     * @param threadFactoryName
     * @return
     */
    public static EventLoopGroup eventLoopGroup(int threads, String threadFactoryName) {
        ThreadFactory threadFactory = new DefaultThreadFactory(threadFactoryName, true);
        return shouldEpoll() ?
                new EpollEventLoopGroup(threads, threadFactory) :
                new NioEventLoopGroup(threads, threadFactory);
    }

    private static boolean shouldEpoll() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }
}
