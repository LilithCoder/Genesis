package com.hatsukoi.genesis.common.timer;

import java.util.concurrent.TimeUnit;

/**
 * 定义定时器的基本行为
 * @author gaoweilin
 * @date 2022/06/16 Thu 1:30 AM
 */
public interface Timer {
    /**
     * 提交一个定时任务（TimerTask）并返回关联的 Timeout 对象
     * @param task
     * @param delay
     * @param unit
     * @return
     */
    Timeout newTimeout(TimerTask task, long delay, TimeUnit unit);
}
