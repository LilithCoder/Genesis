package com.hatsukoi.genesis.registry;

import com.hatsukoi.genesis.common.URL;
import com.hatsukoi.genesis.common.timer.HashedWheelTimer;
import com.hatsukoi.genesis.registry.retry.FailedRegisteredTask;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import static com.hatsukoi.genesis.common.constant.RegistryConstant.*;

/**
 * 重试机制
 * 覆盖了 AbstractRegistry 中 register()/unregister()、subscribe()/unsubscribe() 以及 notify() 这五个核心方法
 * 结合时间轮，实现失败重试的能力
 * 真正与服务发现组件的交互能力则是放到了 doRegister()、doUnregister()、doSubscribe()、doUnsubscribe()、doNotify() 这五个模板方法
 * 由具体子类实现「模板方法模式」
 * @author gaoweilin
 * @date 2022/06/06 Mon 1:10 AM
 */
public abstract class FailbackRegistry extends AbstractRegistry {
    // 注册失败的 URL 集合
    private final ConcurrentMap<URL, FailedRegisteredTask> failedRegistered = new ConcurrentHashMap<URL, FailedRegisteredTask>();
    // 取消注册失败的 URL 集合
    private final ConcurrentMap<URL, FailedUnregisteredTask> failedUnregistered = new ConcurrentHashMap<URL, FailedUnregisteredTask>();
    // 订阅失败 URL 集合
    private final ConcurrentMap<Holder, FailedSubscribedTask> failedSubscribed = new ConcurrentHashMap<Holder, FailedSubscribedTask>();
    // 取消订阅失败的 URL 集合
    private final ConcurrentMap<Holder, FailedUnsubscribedTask> failedUnsubscribed = new ConcurrentHashMap<Holder, FailedUnsubscribedTask>();
    // 通知失败的 URL 集合
    private final ConcurrentMap<Holder, FailedNotifiedTask> failedNotified = new ConcurrentHashMap<Holder, FailedNotifiedTask>();

    /**
     * 重试操作的时间间隔 (毫秒)
     */
    private final int retryPeriod;

    /**
     * 用于定时执行失败重试操作的时间轮
     */
    private final HashedWheelTimer retryTimer;

    public FailbackRegistry(URL url, HashedWheelTimer retryTimer) {
        super(url);
        this.retryPeriod = url.getParam(REGISTRY_RETRY_PERIOD_KEY, DEFAULT_REGISTRY_RETRY_PERIOD);
        this.retryTimer = retryTimer;
    }

    @Override
    public void register(URL url) {
        super.register(url);
        // 将该 Provider URL 从 failedRegistered 集合和 failedUnregistered 集合中删除，并停止相关的重试任务
        // removeFailedRegistered(url);
        // removeFailedUnregistered(url);
        try {
            // 调用 doRegister() 方法，与服务发现组件进行交互
            doRegister(url);
        } catch (Exception e) {

        }
        // 创建重试任务并添加到 failedRegistered 集合和时间轮
         addFailedRegistered(url);
    }

    private void addFailedRegistered(URL url) {
        FailedRegisteredTask oldOne = failedRegistered.get(url);
        if (oldOne != null) {
            return;
        }
        FailedRegisteredTask newTask = new FailedRegisteredTask(url, this);
        oldOne = failedRegistered.putIfAbsent(url, newTask);
        if (oldOne == null) {
            // 如果是新建的重试任务，则提交到时间轮中，等待retryPeriod毫秒后执行
            retryTimer.newTimeout(newTask, retryPeriod, TimeUnit.MILLISECONDS);
        }
    }

    public void removeFailedRegisteredTask(URL url) {
        failedRegistered.remove(url);
    }

    // ================= 模版方法 =================
    public abstract void doRegister(URL url);

}
