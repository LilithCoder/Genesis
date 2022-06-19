package com.hatsukoi.genesis.registry.retry;

import com.hatsukoi.genesis.common.URL;
import com.hatsukoi.genesis.common.timer.Timeout;
import com.hatsukoi.genesis.registry.FailbackRegistry;

/**
 * @author gaoweilin
 * @date 2022/06/18 Sat 5:41 PM
 */
public class FailedRegisteredTask extends AbstractRetryTask {
    private static final String NAME = "retry register";

    public FailedRegisteredTask(URL url, FailbackRegistry registry) {
        super(url, registry, retryTimes, NAME);
    }

    @Override
    protected void doRetry(URL url, FailbackRegistry registry, Timeout timeout) {
        registry.doRegister(url); // 重新注册
        registry.removeFailedRegisteredTask(url); // 删除重试任务
    }
}
