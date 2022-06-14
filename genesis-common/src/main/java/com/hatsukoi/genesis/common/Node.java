package com.hatsukoi.genesis.common;

/**
 * 抽象节点 - 可用来表示Provider/Consumer节点/注册中心节点
 * @author gaoweilin
 * @date 2022/06/03 Fri 7:26 PM
 */
public interface Node {
    /**
     * 返回表示当前节点的 URL
     * @return
     */
    URL getUrl();
    /**
     * 检测当前节点是否可用
     * @return
     */
    boolean isAvailable();
    /**
     * 销毁当前节点并释放底层资源
     */
    void destory();
}
