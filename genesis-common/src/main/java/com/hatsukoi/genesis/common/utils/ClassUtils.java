package com.hatsukoi.genesis.common.utils;

/**
 * @author gaoweilin
 * @date 2022/06/04 Sat 4:26 PM
 */
public class ClassUtils {
    public static ClassLoader getClassLoader(Class<?> clazz) {
        ClassLoader cl = null;
        cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = clazz.getClassLoader();
            if (cl == null) {
                cl = ClassLoader.getSystemClassLoader();
            }
        }
        return cl;
    }
}
