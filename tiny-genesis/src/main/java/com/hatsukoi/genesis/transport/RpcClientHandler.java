package com.hatsukoi.genesis.transport;

import com.hatsukoi.genesis.Utils;
import com.hatsukoi.genesis.protocol.Message;
import com.hatsukoi.genesis.protocol.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import com.hatsukoi.genesis.Constants;

/**
 * Client 读响应 handler
 * @author gaoweilin
 * @date 2022/06/21 Tue 11:51 PM
 */
public class RpcClientHandler extends SimpleChannelInboundHandler<Message<Response>> {

    /**
     * client 处理响应
     * @param channelHandlerContext
     * @param message
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message<Response> message) throws Exception {
        // 获取之前请求暂存对应的 Future
        NettyResponseFuture<Response> respFuture = Connection.REQUEST_FUTURE_MAP.remove(message.getHeader().getMessageId());
        Response resp;
        if (Utils.isHeartBeat(message.getHeader().getBaseInfo())) {
            // 如果是心跳检测的响应
            resp = new Response();
            resp.setCode(Constants.HEARTBEAT_CODE);
        } else {
            // 正常的响应
            resp = message.getContent();
        }
        respFuture.getPromise().setSuccess(resp);
    }
}
