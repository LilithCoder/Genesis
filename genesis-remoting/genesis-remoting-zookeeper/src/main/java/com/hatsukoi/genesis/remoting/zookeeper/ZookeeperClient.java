package com.hatsukoi.genesis.remoting.zookeeper;

import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/06/19 Sun 4:15 AM
 */
public interface ZookeeperClient {
    // 创建 ZNode 节点，还提供了创建临时 ZNode 节点的重载方法
    void create(String path, boolean ephemeral);

    // 获取指定节点的子节点集合
    List<String> getChildren(String path);

    // 获取某个节点存储的内容
    String getContent(String path);

    // 删除节点
    void delete(String path);

    // 关闭当前 ZookeeperClient 实例
    boolean isConnected();

    List<String> addChildListener(String path, ChildListener listener);
    void removeChildListener(String path, ChildListener listener);

    void addDataListener(String path, DataListener listener);
    void removeDataListener(String path, DataListener listener);

    void addStateListener(StateListener listener);
    void removeStateListener(StateListener listener);

    void close();
}
