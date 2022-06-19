package com.hatsukoi.genesis.common.timer;

/**
 * 任务
 * @author gaoweilin
 * @date 2022/06/16 Thu 1:30 AM
 */
public interface TimerTask {
    void run(Timeout timeout) throws Exception;
}
