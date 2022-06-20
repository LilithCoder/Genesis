package com.hatsukoi.genesis.remoting;

import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * @author gaoweilin
 * @date 2022/06/07 Tue 1:53 AM
 */
public interface Server extends Endpoint {
    Collection<Channel> getChannels();

    Channel getChannel(InetSocketAddress remoteAddr);
}
