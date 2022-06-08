package com.hatsukoi.genesis.remoting.transport.netty;

import com.hatsukoi.genesis.common.URL;
import com.hatsukoi.genesis.remoting.ChannelHandler;
import com.hatsukoi.genesis.remoting.Client;
import com.hatsukoi.genesis.remoting.Server;
import com.hatsukoi.genesis.remoting.Transporter;

import java.io.IOException;

/**
 * @author gaoweilin
 * @date 2022/06/07 Tue 2:34 AM
 */
public class NettyTransporter implements Transporter {
    @Override
    public Server bind(URL url, ChannelHandler handler) throws IOException {
        return null;
    }

    @Override
    public Client connect(URL url, ChannelHandler handler) throws IOException {
        return null;
    }
}
