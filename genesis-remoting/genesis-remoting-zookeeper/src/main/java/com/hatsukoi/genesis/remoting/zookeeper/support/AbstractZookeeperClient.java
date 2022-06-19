package com.hatsukoi.genesis.remoting.zookeeper.support;

import com.hatsukoi.genesis.remoting.zookeeper.ZookeeperClient;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;

/**
 * @author gaoweilin
 * @date 2022/06/19 Sun 12:53 PM
 */
public abstract class AbstractZookeeperClient<TargetDataListener, TargetChildListener> implements ZookeeperClient {
    // 缓存了当前 ZookeeperClient 创建的持久 ZNode 节点路径
    private final Set<String> persistentExistNodePath = ConcurrentHashMap.newKeySet();

    // 主要负责监听 Dubbo 与 Zookeeper 集群的连接状态
    private final Set<StateListener> stateListeners = new CopyOnWriteArraySet<StateListener>();

    // 主要监听某个节点存储的数据变化
    private final ConcurrentMap<String, ConcurrentMap<ChildListener, TargetChildListener>> childListeners = new ConcurrentHashMap<String, ConcurrentMap<ChildListener, TargetChildListener>>();

    // 主要监听某个 ZNode 节点下的子节点变化
    private final ConcurrentMap<String, ConcurrentMap<DataListener, TargetDataListener>> dataListeners = new ConcurrentHashMap<String, ConcurrentMap<DataListener, TargetDataListener>>();

    @Override
    public void addDataListener(String path, DataListener listener, Executor executor) {
        ConcurrentMap<DataListener, TargetDataListener> dataListenerMap = dataListeners.computeIfAbsent(path, k -> new ConcurrentHashMap<>());
        TargetDataListener targetListener = dataListenerMap.computeIfAbsent(listener, k -> createTargetDataListener(path, k));
        addTargetDataListener(path, targetListener, executor);
    }

    // 由 AbstractZookeeperClient 的子类实现
    protected abstract TargetDataListener createTargetDataListener(String path, DataListener listener);
    protected abstract void addTargetDataListener(String path, TargetDataListener listener, Executor executor);

}
