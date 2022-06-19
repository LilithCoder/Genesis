package com.hatsukoi.genesis.remoting.zookeeper.curator;

import com.hatsukoi.genesis.common.URL;
import com.hatsukoi.genesis.remoting.zookeeper.ZookeeperClient;
import com.hatsukoi.genesis.remoting.zookeeper.support.AbstractZookeeperTransporter;

/**
 * @author gaoweilin
 * @date 2022/06/19 Sun 4:19 AM
 */
public class CuratorZookeeperTransporter extends AbstractZookeeperTransporter {
    @Override
    public ZookeeperClient createZookeeperClient(URL url) {
        return new CuratorZookeeperClient(url);
    }
}
