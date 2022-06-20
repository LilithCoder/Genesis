package com.hatsukoi.genesis.remoting.transport;

import com.hatsukoi.genesis.remoting.ChannelHandler;

/**
 * @author gaoweilin
 * @date 2022/06/19 Sun 7:31 PM
 */
public class ChannelHandlerDispatcher implements ChannelHandler {
    private final ChannelHandler handler;

    private volatile URL url;

    // closing closed means the process is being closed and close is finished
    private volatile boolean closing;

    private volatile boolean closed;
}
