package com.hatsukoi.genesis.remoting.transport.netty;

import com.hatsukoi.genesis.remoting.transport.AbstractChannel;
import io.netty.channel.ChannelFuture;

/**
 * @author gaoweilin
 * @date 2022/06/20 Mon 2:19 AM
 */
public class NettyChannel extends AbstractChannel {

    /**
     * 通过底层关联的 Netty 框架 Channel，将数据发送到对端
     * @param message
     * @param sent
     * @throws RemotingException
     */
    @Override
    public void send(Object message, boolean sent) throws RemotingException {
        // 调用AbstractChannel的send()方法检测连接是否可用
        super.send(message, sent);

        boolean success = true;
        int timeout = 0;
        try {
            // 依赖Netty框架的Channel发送数据
            ChannelFuture future = channel.writeAndFlush(message);
            if (sent) {
                // 等待发送结束，有超时时间
                timeout = getUrl().getPositiveParameter(TIMEOUT_KEY, DEFAULT_TIMEOUT);
                success = future.await(timeout);
            }
            Throwable cause = future.cause();
            if (cause != null) {
                throw cause;
            }
        } catch (Throwable e) {
            removeChannelIfDisconnected(channel);
            throw new RemotingException(this, "Failed to send message " + PayloadDropper.getRequestWithoutData(message) + " to " + getRemoteAddress() + ", cause: " + e.getMessage(), e);
        }
        if (!success) {
            throw new RemotingException(this, "Failed to send message " + PayloadDropper.getRequestWithoutData(message) + " to " + getRemoteAddress()
                    + "in timeout(" + timeout + "ms) limit");
        }
    }
}
