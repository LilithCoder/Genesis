package com.hatsukoi.genesis.registry;

import com.hatsukoi.genesis.annotation.SPI;

import java.net.InetSocketAddress;

/**
 * @author gaoweilin
 * @date 2022/06/26 Sun 5:44 AM
 */
@SPI
public interface ServiceRegistry {

    void register(String serviceName);
}
