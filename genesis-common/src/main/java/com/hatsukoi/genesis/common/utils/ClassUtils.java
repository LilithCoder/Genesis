package com.hatsukoi.genesis.common.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author gaoweilin
 * @date 2022/06/04 Sat 4:26 PM
 */
public class ClassUtils {
    /**
     * 获取类加载器
     * @param clazz
     * @return
     */
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

    /**
     * 是否为setter方法
     * @param method
     * @return
     */
    public static boolean isSetter(Method method) {
        return method.getName().startsWith("set") &&
                method.getParameterTypes().length == 1 &&
                Modifier.isPublic(method.getModifiers());
    }

    /**
     * 获取setter的属性
     * setVersion -> version
     * @param method
     * @return
     */
    public static String getSetterProperty(Method method) {
        return method.getName().length() > 3 ? method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4) : "";
    }
}
