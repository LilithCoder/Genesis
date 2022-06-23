package com.hatsukoi.genesis.transport;

import com.hatsukoi.genesis.protocol.Message;
import com.hatsukoi.genesis.protocol.Request;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Promise;

/**
 * 异步请求的Future结果
 * @author gaoweilin
 * @date 2022/06/22 Wed 1:41 AM
 */
public class NettyResponseFuture<T> {
    /**
     * 生成时间
     */
    private long createTime;
    /**
     * 过期时间
     */
    private long timeOut;
    /**
     * 对应的请求
     */
    private Message<Request> request;
    /**
     * 对应的 channel
     */
    private Channel channel;
    /**
     * 异步Promise
     */
    private Promise<T> promise;

    public NettyResponseFuture(long createTime, long timeOut, Message<Request> request, Channel channel, Promise<T> promise) {
        this.createTime = createTime;
        this.timeOut = timeOut;
        this.request = request;
        this.channel = channel;
        this.promise = promise;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }

    public Message<Request> getRequest() {
        return request;
    }

    public void setRequest(Message<Request> request) {
        this.request = request;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Promise<T> getPromise() {
        return promise;
    }

    public void setPromise(Promise<T> promise) {
        this.promise = promise;
    }
}
