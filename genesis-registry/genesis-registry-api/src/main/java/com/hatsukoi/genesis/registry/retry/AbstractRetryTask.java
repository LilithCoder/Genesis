package com.hatsukoi.genesis.registry.retry;

import com.hatsukoi.genesis.common.URL;
import com.hatsukoi.genesis.common.timer.Timeout;
import com.hatsukoi.genesis.common.timer.Timer;
import com.hatsukoi.genesis.common.timer.TimerTask;
import com.hatsukoi.genesis.registry.FailbackRegistry;

import java.util.concurrent.TimeUnit;

/**
 * AbstractRetryTask 抽象重试任务
 * AbstractRetryTask 中维护了当前任务关联的 URL、当前重试的次数等信息
 * @author gaoweilin
 * @date 2022/06/18 Sat 5:41 PM
 */
public abstract class AbstractRetryTask implements TimerTask {
    /**
     * url for retry task
     */
    protected final URL url;

    /**
     * registry for this task
     */
    protected final FailbackRegistry registry;

    private int times = 1;

    /**
     * define the most retry times
     */
    private final int retryTimes;
    /**
     * retry period
     */
    final long retryPeriod;

    protected AbstractRetryTask(URL url, FailbackRegistry registry, int retryTimes, long retryPeriod) {
        this.url = url;
        this.registry = registry;
        this.retryTimes = retryTimes;
        this.retryPeriod = retryPeriod;
    }

    // AbstractRetryTask 将 doRetry() 方法作为抽象方法，留给子类实现具体的重试逻辑
    protected abstract void doRetry(URL url, FailbackRegistry registry, Timeout timeout);

    @Override
    public void run(Timeout timeout) throws Exception {
        if (times > retryTimes) {
            return;
        }
        try {
            // 执行重试
            doRetry(url, registry, timeout);
        } catch (Throwable t) {
            // 重新添加定时任务，等待重试
            reput(timeout, retryPeriod);
        }
    }

    protected void reput(Timeout timeout, long tick) {
        if (timeout == null) {
            throw new IllegalArgumentException();
        }

        Timer timer = timeout.timer();
        times++;
        // 添加定时任务
        timer.newTimeout(timeout.task(), tick, TimeUnit.MILLISECONDS);
    }
}
