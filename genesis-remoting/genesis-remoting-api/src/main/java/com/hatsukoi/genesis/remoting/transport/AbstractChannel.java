package com.hatsukoi.genesis.remoting.transport;

import com.hatsukoi.genesis.remoting.Channel;

/**
 * @author gaoweilin
 * @date 2022/06/19 Sun 8:11 PM
 */
public abstract class AbstractChannel extends AbstractPeer implements Channel {
    public AbstractChannel(URL url, ChannelHandler handler) {
        super(url, handler);
    }

    @Override
    public void send(Object message, boolean sent) throws RemotingException {
        if (isClosed()) {
            throw new RemotingException(this, "Failed to send message "
                    + (message == null ? "" : message.getClass().getName()) + ":" + PayloadDropper.getRequestWithoutData(message)
                    + ", cause: Channel closed. channel: " + getLocalAddress() + " -> " + getRemoteAddress());
        }
    }

    @Override
    public String toString() {
        return getLocalAddress() + " -> " + getRemoteAddress();
    }
}
