package com.hatsukoi.genesis.remoting;

import com.hatsukoi.genesis.common.URL;
import com.hatsukoi.genesis.common.extension.ExtensionLoader;

/**
 * Transporter facade 门面类
 * 通过 Transporters 门面类获取到 Transporter 的具体扩展实现
 * 通过 Transporter 拿到相应的 Client 和 RemotingServer 实现，就可以建立（或接收）Channel 与远端进行交互了
 * @author gaoweilin
 * @date 2022/06/07 Tue 2:38 AM
 */
public class Transporters {

    // 自动生成Transporter适配器并加载
    public static Transporter getTransporter() {
        return ExtensionLoader.getExtensionLoader(Transporter.class).getAdaptiveExtension();
    }

    public static RemotingServer bind(URL url, ChannelHandler... handlers) throws RemotingException {
        if (url == null) {
            throw new IllegalArgumentException("url == null");
        }
        if (handlers == null || handlers.length == 0) {
            throw new IllegalArgumentException("handlers == null");
        }
        ChannelHandler handler;
        if (handlers.length == 1) {
            handler = handlers[0];
        } else {
            handler = new ChannelHandlerDispatcher(handlers);
        }
        return getTransporter().bind(url, handler);
    }

    public static Client connect(URL url, ChannelHandler... handlers) throws RemotingException {
        if (url == null) {
            throw new IllegalArgumentException("url == null");
        }
        ChannelHandler handler;
        if (handlers == null || handlers.length == 0) {
            handler = new ChannelHandlerAdapter();
        } else if (handlers.length == 1) {
            handler = handlers[0];
        } else {
            handler = new ChannelHandlerDispatcher(handlers);
        }
        return getTransporter().connect(url, handler);
    }
}
