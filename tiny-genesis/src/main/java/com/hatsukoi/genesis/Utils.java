package com.hatsukoi.genesis;

/**
 * @author gaoweilin
 * @date 2022/06/21 Tue 4:37 AM
 */
public class Utils {
    /**
     * 是否为心跳消息
     * @param baseInfo
     * @return
     */
    public static boolean isHeartBeat(byte baseInfo) {
        return (baseInfo & 32) != 0;
    }

    /**
     * 是否为请求类型的消息
     * @param baseInfo
     * @return
     */
    public static boolean isRequest(byte baseInfo) {
        return (baseInfo & 1) != 1;
    }
}
