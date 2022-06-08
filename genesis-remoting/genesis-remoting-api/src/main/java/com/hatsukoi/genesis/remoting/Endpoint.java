package com.hatsukoi.genesis.remoting;

import com.hatsukoi.genesis.common.URL;

import java.net.InetSocketAddress;

/**
 * @author gaoweilin
 * @date 2022/06/07 Tue 1:01 AM
 */
public interface Endpoint {
    /**
     * url
     * @return
     */
    URL getUrl();

    ChannelHandler getChannelHandler();
    /**
     * get ip address + port number
     */
    InetSocketAddress getLocalAddress();
    /**
     * send message
     */
    void send(Object message);
    /**
     * close the channel
     */
    void close();
    /**
     * return is closed
     */
    boolean isClosed();
}
