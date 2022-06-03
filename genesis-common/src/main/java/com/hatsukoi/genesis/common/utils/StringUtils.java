package com.hatsukoi.genesis.common.utils;

/**
 * @author gaoweilin
 * @date 2022/06/03 Fri 7:50 PM
 */
public class StringUtils {
    /**
     * check if the target string is empty
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
}
