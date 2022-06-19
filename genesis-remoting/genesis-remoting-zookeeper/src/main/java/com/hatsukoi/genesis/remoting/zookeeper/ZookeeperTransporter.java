package com.hatsukoi.genesis.remoting.zookeeper;

import com.hatsukoi.genesis.common.URL;
import com.hatsukoi.genesis.common.extension.Adaptive;
import com.hatsukoi.genesis.common.extension.SPI;

/**
 * @author gaoweilin
 * @date 2022/06/18 Sat 6:23 PM
 */
@SPI("curator")
public interface ZookeeperTransporter {

    // 创建 ZookeeperClient 对象
    @Adaptive({"client", "transporter"})
    ZookeeperClient connect(URL url);
}
