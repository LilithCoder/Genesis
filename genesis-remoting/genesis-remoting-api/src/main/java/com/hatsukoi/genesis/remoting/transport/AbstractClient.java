package com.hatsukoi.genesis.remoting.transport;

import com.hatsukoi.genesis.common.URL;
import com.hatsukoi.genesis.remoting.Channel;
import com.hatsukoi.genesis.remoting.ChannelHandler;
import com.hatsukoi.genesis.remoting.Client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author gaoweilin
 * @date 2022/06/01 Wed 5:20 AM
 */
public class AbstractClient extends AbstractEndpoint implements Client {
    // 在 Client 底层进行连接、断开、重连等操作时，需要获取该锁进行同步
    private final Lock connectLock = new ReentrantLock();

    // 在发送数据之前，会检查 Client 底层的连接是否断开，如果断开了，则会根据 needReconnect 字段，决定是否重连
    private final boolean needReconnect;

    // 当前 Client 关联的线程池
    protected volatile ExecutorService executor;

    public AbstractClient(URL url, ChannelHandler handler) throws RemotingException {
        super(url, handler);
        // 解析URL，初始化needReconnect值
        needReconnect = url.getParameter(Constants.SEND_RECONNECT_KEY, false);
        // 解析URL，初始化executor
        initExecutor(url);

        try {
            // 初始化底层的NIO库的相关组件
            doOpen();
        } catch (Throwable t) {
            close();
            throw new RemotingException(url.toInetSocketAddress(), null,
                    "Failed to start " + getClass().getSimpleName() + " " + NetUtils.getLocalAddress()
                            + " connect to the server " + getRemoteAddress() + ", cause: " + t.getMessage(), t);
        }
        try {
            // 创建底层连接
            connect();
            if (logger.isInfoEnabled()) {
                logger.info("Start " + getClass().getSimpleName() + " " + NetUtils.getLocalAddress() + " connect to the server " + getRemoteAddress());
            }
        } catch (RemotingException t) {
            if (url.getParameter(Constants.CHECK_KEY, true)) {
                close();
                throw t;
            } else {
                logger.warn("Failed to start " + getClass().getSimpleName() + " " + NetUtils.getLocalAddress()
                        + " connect to the server " + getRemoteAddress() + " (check == false, ignore and retry later!), cause: " + t.getMessage(), t);
            }
        } catch (Throwable t) {
            close();
            throw new RemotingException(url.toInetSocketAddress(), null,
                    "Failed to start " + getClass().getSimpleName() + " " + NetUtils.getLocalAddress()
                            + " connect to the server " + getRemoteAddress() + ", cause: " + t.getMessage(), t);
        }
    }
    protected abstract void doOpen() throws Throwable;

    private void initExecutor(URL url) {
        url = ExecutorUtil.setThreadName(url, CLIENT_THREAD_POOL_NAME);
        url = url.addParameterIfAbsent(THREADPOOL_KEY, DEFAULT_CLIENT_THREADPOOL);
        executor = executorRepository.createExecutorIfAbsent(url);
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
