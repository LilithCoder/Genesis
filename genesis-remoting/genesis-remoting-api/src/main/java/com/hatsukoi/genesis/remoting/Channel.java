package com.hatsukoi.genesis.remoting;

import java.net.InetSocketAddress;

/**
 * @author gaoweilin
 * @date 2022/06/07 Tue 1:14 AM
 */
public interface Channel extends Endpoint {
    InetSocketAddress getRemoteAddress();

    boolean isConnected();

    Object getAttribute(String key);

    void setAttribute(String key, Object value);
}
