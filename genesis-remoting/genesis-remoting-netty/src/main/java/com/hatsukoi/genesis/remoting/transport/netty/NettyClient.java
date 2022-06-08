package com.hatsukoi.genesis.remoting.transport.netty;

import com.hatsukoi.genesis.common.URL;
import com.hatsukoi.genesis.remoting.ChannelHandler;
import com.hatsukoi.genesis.remoting.transport.AbstractClient;

/**
 * @author gaoweilin
 * @date 2022/06/01 Wed 5:19 AM
 */
public class NettyClient extends AbstractClient {
    public NettyClient(URL url, ChannelHandler handler) {
        super(url, handler);
    }
}
