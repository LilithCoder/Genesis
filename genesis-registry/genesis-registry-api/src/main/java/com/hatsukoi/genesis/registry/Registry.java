package com.hatsukoi.genesis.registry;

import com.hatsukoi.genesis.common.Node;
import com.hatsukoi.genesis.common.URL;

/**
 * 表示一个拥有注册中心能力的节点
 * 继承了Node节点、继承了RegistryService有注册中心能力
 * 实际是本地的注册中心客户端，但代理了远程实际的注册中心
 * @author gaoweilin
 * @date 2022/06/05 Sun 1:53 AM
 */
public interface Registry extends Node, RegistryService {
    default void reExportRegister(URL url) {
        register(url);
    }
    default void reExportUnregister(URL url) {
        unregister(url);
    }
}
