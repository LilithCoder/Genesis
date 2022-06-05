package com.hatsukoi.genesis.registry;

import com.hatsukoi.genesis.common.URL;
import com.hatsukoi.genesis.common.extension.Adaptive;
import com.hatsukoi.genesis.common.extension.SPI;

/**
 * Registry 的工厂接口，负责创建 Registry 对象
 * @author gaoweilin
 * @date 2022/06/05 Sun 2:00 AM
 */
@SPI("genesis") // 指定了默认的扩展名
public interface RegistryFactory {

    /**
     * 表示会生成适配器类并根据 URL 参数中的 protocol 参数值选择相应的实现
     * @param url
     * @return
     */
    @Adaptive("protocol")
    Registry getRegistry(URL url);
}
