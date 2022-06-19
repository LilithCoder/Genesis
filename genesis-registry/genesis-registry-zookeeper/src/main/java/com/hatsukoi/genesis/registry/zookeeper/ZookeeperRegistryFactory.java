package com.hatsukoi.genesis.registry.zookeeper;

import com.hatsukoi.genesis.common.URL;
import com.hatsukoi.genesis.registry.AbstractRegistryFactory;
import com.hatsukoi.genesis.registry.Registry;
import com.hatsukoi.genesis.remoting.zookeeper.ZookeeperTransporter;

/**
 * @author gaoweilin
 * @date 2022/06/15 Wed 1:46 AM
 */
public class ZookeeperRegistryFactory extends AbstractRegistryFactory {

    private ZookeeperTransporter zookeeperTransporter;

    /**
     * Invisible injection of zookeeper client via IOC/SPI
     * @param zookeeperTransporter
     */
    public void setZookeeperTransporter(ZookeeperTransporter zookeeperTransporter) {
        this.zookeeperTransporter = zookeeperTransporter;
    }

    @Override
    public Registry createRegistry(URL url) {
        return new ZookeeperRegistry(url, zookeeperTransporter);
    }

}
