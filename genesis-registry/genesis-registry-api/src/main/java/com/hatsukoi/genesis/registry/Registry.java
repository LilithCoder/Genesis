package com.hatsukoi.genesis.registry;

import com.hatsukoi.genesis.common.Node;
import com.hatsukoi.genesis.common.URL;

/**
 * 表示的就是一个拥有注册中心能力的节点
 * @author gaoweilin
 * @date 2022/06/05 Sun 1:53 AM
 */
public interface Registry extends Node, RegistryService {
    default void doRegister(URL url) {
        register(url);
    }
    default void doUnregister(URL url) {
        unregister(url);
    }
}
