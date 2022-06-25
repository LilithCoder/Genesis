package com.hatsukoi.genesis.factory;

import com.hatsukoi.genesis.annotation.Bean;
import com.hatsukoi.genesis.annotation.RpcScan;
import com.hatsukoi.genesis.annotation.RpcService;
import com.hatsukoi.genesis.extension.ExtensionLoader;
import com.hatsukoi.genesis.registry.ServiceProvider;
import com.hatsukoi.genesis.registry.ServiceRegistry;
import com.hatsukoi.genesis.utils.*;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bean工厂（单例对象的工厂类）
 * 任何Bean都可以放进来
 * @author gaoweilin
 * @date 2022/06/22 Wed 12:32 AM
 */
public class BeanFactory {
    private static Logger logger = Logger.getLogger(BeanFactory.class);
    /**
     * Bean实例缓存
     * key   为服务名
     * value 为bean实例
     */
    private static Map<String, Object> BEAN_MAP = new ConcurrentHashMap<>();

    /**
     * Bean工厂初始化
     */
    public BeanFactory() {
        // 实例化一个服务注册模块
        ServiceRegistry zkServiceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension("zookeeper");
        // 实例化一个本地服务注册表
        ServiceProvider serviceProvider = ExtensionLoader.getExtensionLoader(ServiceProvider.class).getExtension("");
        // 扫描服务
        // 边界条件：启动类有没有ServiceScan注解
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;
        try {
            startClass = Class.forName(mainClassName);
            if(!startClass.isAnnotationPresent(RpcScan.class)) {
                String msg = "启动类缺少 @RpcScan 注解";
                logger.error(msg);
                throw new IllegalStateException(msg);
            }
        } catch (Throwable t) {
            logger.error("", t);
            throw new IllegalStateException("");
        }
        // 扫描基础包路径下@Bean注释、@RpcService注释的类，都加入Bean实例缓存中
        String basePackage = startClass.getAnnotation(RpcScan.class).basePackage();
        if("".equals(basePackage)) {
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }
        Set<Class<?>> classSet = ReflectUtil.scanClasses(basePackage);

        // 如果类的声明字段被RpcReference注释，实例化Client代理对象，这个声明字段被赋值代理对象
        for(Class<?> clazz : classSet) {
            if (clazz.isAnnotationPresent(Bean.class) || clazz.isAnnotationPresent(RpcService.class)) {
                // 实例化并缓存Bean
                String key = clazz.getName().toString();
                Object obj;
                try {
                    obj = clazz.newInstance();
                } catch (Throwable t) {
                    logger.error("创建 " + clazz + " 时有错误发生", t);
                    continue;
                }
                BEAN_MAP.put(key, obj);
            }
            // 如果RpcService注释，添加到服务注册表，注册到远程注册中心
            if (clazz.isAnnotationPresent(RpcService.class)) {
                zkServiceRegistry.register();
                serviceProvider.add(key, obj);
            }

            if (clazz.isAnnotationPresent(RpcService.class)) {

            }
        }
    }

    /**
     * Bean工厂获取Bean（缓存or反射实例化）
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {
        if (clazz == null) {
            String msg = "尝试获取类型为null的Bean";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }
        String key = clazz.toString();
        if (BEAN_MAP.containsKey(key)) {
            return clazz.cast(BEAN_MAP.get(key));
        } else {
            return clazz.cast(BEAN_MAP.computeIfAbsent(key, k -> {
                try {
                    clazz.getDeclaredConstructor().newInstance();
                } catch (Throwable t) {
                    String msg = "反射实例化Bean失败";
                    logger.error(msg);
                    throw new IllegalStateException(msg);
                }
            }));
        }
    }
}
