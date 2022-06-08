package com.hatsukoi.genesis.remoting.transport;

import com.hatsukoi.genesis.common.URL;
import com.hatsukoi.genesis.remoting.Channel;
import com.hatsukoi.genesis.remoting.ChannelHandler;
import com.hatsukoi.genesis.remoting.Client;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author gaoweilin
 * @date 2022/06/01 Wed 5:20 AM
 */
public class AbstractClient extends AbstractEndpoint implements Client {
    public AbstractClient(URL url, ChannelHandler handler) {
        super(url, handler);
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return null;
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public Object getAttribute(String key) {
        return null;
    }

    @Override
    public void setAttribute(String key, Object value) {

    }

    @Override
    public void connected(Channel channel) {

    }

    @Override
    public void disconnected(Channel channel) {

    }

    @Override
    public void sent(Channel channel, Object message) {

    }

    @Override
    public void received(Channel channel, Object message) {

    }

    @Override
    public void caught(Channel channel, Throwable exception) {

    }

    @Override
    public void reconnect() throws IOException {

    }

    @Override
    public URL getUrl() {
        return null;
    }

    @Override
    public ChannelHandler getChannelHandler() {
        return null;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return null;
    }

    @Override
    public void send(Object message) {

    }

    @Override
    public void close() {

    }

    @Override
    public boolean isClosed() {
        return false;
    }
}
