package com.hatsukoi.genesis.remoting.exchange;

import java.util.concurrent.CompletableFuture;

/**
 * @author gaoweilin
 * @date 2022/06/21 Tue 12:53 AM
 */
public interface ExchangeHandler extends ChannelHandler, TelnetHandler {

    /**
     * reply.
     *
     * @param channel
     * @param request
     * @return response
     * @throws RemotingException
     */
    CompletableFuture<Object> reply(ExchangeChannel channel, Object request) throws RemotingException;

}
