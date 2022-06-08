package com.hatsukoi.genesis.remoting.transport;

import com.hatsukoi.genesis.common.URL;
import com.hatsukoi.genesis.common.extension.ExtensionLoader;
import com.hatsukoi.genesis.common.threadpool.ExecutorRepository;
import com.hatsukoi.genesis.remoting.Channel;
import com.hatsukoi.genesis.remoting.ChannelHandler;
import com.hatsukoi.genesis.remoting.Server;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.concurrent.ExecutorService;

import static com.hatsukoi.genesis.common.constant.remoting.RemotingConstant.BIND_IP_KEY;
import static com.hatsukoi.genesis.common.constant.remoting.RemotingConstant.BIND_PORT_KEY;

/**
 * @author gaoweilin
 * @date 2022/06/01 Wed 5:20 AM
 */
public abstract class AbstractServer extends AbstractEndpoint implements Server {

    private InetSocketAddress localAddress;
    private InetSocketAddress bindAddress;
    private int accepts;
    /**
     * 负责管理线程池
     */
    private ExecutorRepository executorRepository = ExtensionLoader.getExtensionLoader(ExecutorRepository.class).getDefaultExtension();

    ExecutorService executor;

    public AbstractServer(URL url, ChannelHandler handler) {
        super(url, handler);
        localAddress = getUrl().toInetSocketAddress();
        String bindIp = getUrl().getParam(BIND_IP_KEY, getUrl().getHost());
        int bindPort = getUrl().getParam(BIND_PORT_KEY, getUrl().getPort());
        bindAddress = new InetSocketAddress(bindIp, bindPort);
        try {
            doOpen();
        } catch (Throwable t) {
            //
        }
    }

    protected abstract void doOpen() throws Throwable;
}
