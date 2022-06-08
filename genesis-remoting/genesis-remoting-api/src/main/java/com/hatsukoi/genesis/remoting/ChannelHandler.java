package com.hatsukoi.genesis.remoting;

import com.hatsukoi.genesis.common.extension.SPI;

/**
 * 注册在 Channel 上的消息处理器
 * @author gaoweilin
 * @date 2022/06/07 Tue 1:10 AM
 */
@SPI
public interface ChannelHandler {
    /**
     * 处理 Channel 的连接建立事件
     * @param channel
     */
    void connected(Channel channel);

    /**
     * 处理 Channel 的连接断开事件
     * @param channel
     */
    void disconnected(Channel channel);

    /**
     * 处理发送的数据
     * @param channel
     * @param message
     */
    void sent(Channel channel, Object message);

    /**
     * 处理读取到的数据
     * @param channel
     * @param message
     */
    void received(Channel channel, Object message);

    /**
     * 处理捕获到的异常
     * @param channel
     * @param exception
     */
    void caught(Channel channel, Throwable exception);
}
