package com.hatsukoi.genesis.transport;

import com.hatsukoi.genesis.protocol.Message;
import com.hatsukoi.genesis.protocol.Request;
import com.hatsukoi.genesis.protocol.Response;
import com.hatsukoi.genesis.transport.netty.NettyResponseFuture;
import io.netty.channel.ChannelFuture;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author gaoweilin
 * @date 2022/06/22 Wed 1:37 AM
 */
public class Connection implements Closeable {
    /**
     * 请求消息ID <-> 响应Future 映射
     */
    public static final Map<Long, NettyResponseFuture<Response>> REQUEST_FUTURE_MAP = new ConcurrentHashMap<>();
    /**
     * 用于生成全局唯一消息ID
     */
    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);
    /**
     * 该Channel的异步操作结果
     */
    private ChannelFuture future;

    public Connection(ChannelFuture future) {
        this.future = future;
    }

    public NettyResponseFuture<Response> request(Message<Request> message, long timeout) {
        // 生成并设置消息ID
        long messageId = ID_GENERATOR.incrementAndGet();
        message.getHeader().setMessageId(messageId);
        // 创建消息关联的Future
        NettyResponseFuture respFuture = new NettyResponseFuture(
                System.currentTimeMillis(),
                timeout,
                message,
                future.channel(),
                new DefaultPromise(new DefaultEventLoop()));
        // 将消息ID和关联的 Future 记录到 REQUEST_FUTURE_MAP 集合中
        REQUEST_FUTURE_MAP.put(messageId, respFuture);
        try {
            future.channel().writeAndFlush(message);
        } catch (Throwable t) {
            // 发送请求异常时，删除对应的Future
            REQUEST_FUTURE_MAP.remove(messageId);
        }
        return respFuture;
    }

    @Override
    public void close() throws IOException {
        future.channel().close();
    }
}
