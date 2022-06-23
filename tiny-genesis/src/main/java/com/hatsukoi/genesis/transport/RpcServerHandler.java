package com.hatsukoi.genesis.transport;

import com.hatsukoi.genesis.Utils;
import com.hatsukoi.genesis.protocol.Message;
import com.hatsukoi.genesis.protocol.Request;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Server 读请求 handler
 * @author gaoweilin
 * @date 2022/06/21 Tue 11:50 PM
 */
public class RpcServerHandler extends SimpleChannelInboundHandler<Message<Request>> {
    /**
     * 业务线程池
     */
    private static Executor executor = Executors.newCachedThreadPool();

    /**
     * 处理读到的请求
     * @param ctx
     * @param message
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message<Request> message) throws Exception {
        byte baseInfo = message.getHeader().getBaseInfo();
        if (Utils.isHeartBeat(baseInfo)) {
            // 如果是心跳消息，直接返回
            ctx.writeAndFlush(message);
            return;
        } else {
            // 如果是正常业务请求，封装成Runnable提交到业务线程池
            executor.execute(new InvokeRunnable(message, ctx));
        }
    }
}
