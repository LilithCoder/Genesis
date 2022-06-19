package com.hatsukoi.genesis.common.timer;

/**
 * @author gaoweilin
 * @date 2022/06/16 Thu 1:30 AM
 */
public interface Timeout {
    /**
     * 返回创建此句柄的 {@link Timer}。
     */
    Timer timer();

    /**
     * 返回与此句柄关联的 {@link TimerTask}。
     */
    TimerTask task();

    /**
     * 当且仅当与此句柄关联的 {@link TimerTask} 已过期时，才返回 {@code true}。
     */
    boolean isExpired();

    /**
     * 当且仅当与此句柄关联的 {@link TimerTask} 已被取消时，才返回 {@code true}。
     */
    boolean isCancelled();

    /**
     * 尝试取消与此句柄关联的 {@link TimerTask}
     * 如果任务已经执行或取消，它将返回而没有副作用
     */
    boolean cancel();
}
