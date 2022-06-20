package com.hatsukoi.genesis.remoting.exchange;

import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * @author gaoweilin
 * @date 2022/06/21 Tue 12:53 AM
 */
public interface ExchangeServer extends RemotingServer {

    /**
     * get channels.
     *
     * @return channels
     */
    Collection<ExchangeChannel> getExchangeChannels();

    /**
     * get channel.
     *
     * @param remoteAddress
     * @return channel
     */
    ExchangeChannel getExchangeChannel(InetSocketAddress remoteAddress);

}
