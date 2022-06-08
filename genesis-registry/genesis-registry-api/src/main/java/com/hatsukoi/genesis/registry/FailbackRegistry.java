package com.hatsukoi.genesis.registry;

import com.hatsukoi.genesis.common.URL;

import static com.hatsukoi.genesis.common.constant.RegistryConstant.*;

/**
 * @author gaoweilin
 * @date 2022/06/06 Mon 1:10 AM
 */
public abstract class FailbackRegistry extends AbstractRegistry {
    /**
     * 重试操作的时间间隔 (毫秒)
     */
    private final int retryPeriod;

    public FailbackRegistry(URL url) {
        super(url);
        this.retryPeriod = url.getParam(REGISTRY_RETRY_PERIOD_KEY, DEFAULT_REGISTRY_RETRY_PERIOD);

    }
}
