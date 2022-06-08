package com.hatsukoi.genesis.remoting.transport;

import com.hatsukoi.genesis.common.URL;
import com.hatsukoi.genesis.remoting.ChannelHandler;
import com.hatsukoi.genesis.remoting.Endpoint;

/**
 * @author gaoweilin
 * @date 2022/06/07 Tue 3:56 AM
 */
public abstract class AbstractPeer implements Endpoint, ChannelHandler {
    private volatile URL url;

    private volatile boolean closing;

    private volatile boolean closed;

    private final ChannelHandler handler;

    protected AbstractPeer(URL url, ChannelHandler handler) {
        this.url = url;
        this.handler = handler;
    }

    @Override
    public URL getUrl() {
        return url;
    }
}
