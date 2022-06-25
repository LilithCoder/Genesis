package com.hatsukoi.genesis.extension;

import com.hatsukoi.genesis.annotation.SPI;
import com.hatsukoi.genesis.utils.*;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Genesis 的扩展加载机制
 * @author gaoweilin
 * @date 2022/06/04 Sat 12:03 AM
 */
public class ExtensionLoader<T> {

    // ======================== 静态变量 ========================

    private static final String SPI_DIR = "META-INF/genesis/";
    private static final Logger logger = Logger.getLogger(ExtensionLoader.class);

    // ======================== 成员变量 ========================

    /**
     * 缓存了扩展接口与加载其扩展实现的 ExtensionLoader 实例之间的映射关系
     */
    private static final ConcurrentMap<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>(64);
    /**
     * 缓存了扩展实现类与其实例对象的映射关系
     */
    private static final ConcurrentMap<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>(64);
    /**
     * 当前 ExtensionLoader 实例负责加载扩展接口
     */
    private final Class<?> type;
    /**
     * 记录了 type 这个扩展接口上 @SPI 注解的 value 值，也就是默认扩展名
     */
    private String cachedDefaultName;
    /**
     * 缓存了该 ExtensionLoader 加载的扩展名与扩展实现类之间的映射关系
     */
    private ConcurrentMap<String, Class<?>> cachedClasses = new ConcurrentHashMap<>();
    /**
     * 缓存了该 ExtensionLoader 加载的扩展名与扩展实现对象之间的映射关系
     */
    private ConcurrentMap<String, Object> cachedInstances = new ConcurrentHashMap<>();
    /**
     * 适配器扩展实现类缓存
     */
    private volatile Class<?> cachedAdaptiveClass;
    /**
     * 适配器扩展实现实例缓存
     */
    private Object cachedAdaptiveInstance;

    private ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    // ======================== 公共方法 ========================

    /**
     * 根据扩展接口去获取其扩展加载需要的 ExtensionLoader 实例
     * @param type  扩展接口
     * @param <T>
     * @return      ExtensionLoader 实例
     */
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        // 1. 边界场景
        if (type == null) {
            String errMsg = "Extension type == null";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }
        if (!type.isInterface()) {
            String errMsg = "Extension type (" + type + ") is not an interface";
            throw new IllegalArgumentException(errMsg);
        }
        if (!type.isAnnotationPresent(SPI.class)) {
            String errMsg = "Extension type (" + type + ") is not an extension interface, because " +
                    "it is NOT annotated with @" + SPI.class.getSimpleName();
            throw new IllegalArgumentException(errMsg);
        }
        // 2. 从缓存里获取 ExtensionLoader，如果没有就实例化一个并存入缓存
        ExtensionLoader<T> loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        if (loader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<T>(type));
            loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        }
        return loader;
    }

    /**
     * 获取 ExtensionLoader 实例后根据扩展名去获取扩展实现的实例
     * @param name  扩展名
     * @return      扩展实现的实例
     */
    public T getExtension(String name) {
        // 1. 传入扩展名为空，则获取默认实现
        if (StringUtils.isEmpty(name)) {
            return getDefaultExtension();
        }
        // 2. 根据传入的扩展名称从 cachedInstances 缓存中查找扩展实现的实例
        //    没有缓存的话查询相应扩展实现类并实例化
        Object instance = cachedInstances.get(name);
        if (instance == null) {
            synchronized (cachedInstances) {
                instance = cachedInstances.get(name);
                if (instance == null) {
                    T extInstance = createExtension(name);
                    cachedInstances.putIfAbsent(name, extInstance);
                }
            }
        }
        return (T) instance;
    }

    // ======================== 私有方法 ========================

    /**
     * 获取默认扩展实现的实例
     * @return
     */
    private T getDefaultExtension() {
        // 1. 获取默认扩展名
        cacheDefaultExtensionName();
        if (StringUtils.isEmpty(cachedDefaultName)) {
            return null;
        }
        // 2. 根据默认扩展名获取了扩展实现的实例
        return getExtension(cachedDefaultName);
    }

    /**
     * 获取默认扩展名
     */
    private void cacheDefaultExtensionName() {
        SPI defaultAnnotation = type.getAnnotation(SPI.class);
        if (defaultAnnotation != null) {
            String value = defaultAnnotation.value().trim();
            cachedDefaultName = value;
        }
    }

    /**
     * 根据扩展名获取扩展实现类的实例
     * @param name
     * @return
     */
    private T createExtension(String name) {
        try {
            // 1. 根据扩展名去获取扩展实现类型
            Class<?> clazz = getExtensionClasses().get(name);
            if (clazz == null) {
                throw new RuntimeException("Cannot find the extension class according to name \"" + name +"\"");
            }
            // 2. 先拿缓存里的扩展实现类实例
            T instance = (T) EXTENSION_INSTANCES.get(clazz);
            if (instance == null) {
                // 没有的话就新建一个实例对象
                EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.newInstance());
                instance = (T) EXTENSION_INSTANCES.get(clazz);
            }
//            // 3. 自动装配新建的实例对象
//            injectExtensionInstance(instance);
            return instance;
        } catch (Throwable t) {
            String errMsg = "Extension instance (name: " + name + ", class: " + type + ") couldn't be instantiated: ";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg, t);
        }
    }

    /**
     * 获取所有扩展实现类 (缓存or加载)
     */
    private Map<String, Class<?>> getExtensionClasses() {
        // 1. 先从缓存里获取扩展实现类
        Map<String, Class<?>> classes = cachedClasses;
        // 2. 缓存里没有，则同步加载扩展实现类
        if (classes == null) {
            synchronized (cachedClasses) {
                classes = cachedClasses;
                if (classes == null) {
                    // 同步加载扩展实现类并缓存
                    classes = loadExtensionClasses();
                    cachedClasses = (ConcurrentMap<String, Class<?>>) classes;
                }
            }
        }
        return classes;
    }

    /**
     * 同步加载扩展实现类
     * @return
     */
    private Map<String, Class<?>> loadExtensionClasses() {
        // 加载获得的扩展实现类
        Map<String, Class<?>> extensionClasses = new HashMap<>();
        // SPI 目录
        String fileName = SPI_DIR + type;
        try {
            // 获取类加载器
            ClassLoader cl = ClassUtils.getClassLoader(ExtensionLoader.class);
            // 加载配置文件
            Enumeration<URL> resources = cl.getResources(fileName);
            // 遍历 SPI 配置文件，加载其中的扩展实现类
            if (resources != null) {
                while (resources.hasMoreElements()) {
                    URL url = resources.nextElement();
                    loadClassViaConfigFile(extensionClasses, url, cl);
                }
            }
        } catch (Exception e) {
            String errMsg = "Exception occurrs when loading extension class (interface: " + type + ") from file:"
                    + fileName;
            logger.error(errMsg, e);
            throw new IllegalStateException(errMsg);
        }
        return extensionClasses;
    }

    /**
     * 加载 SPI 配置文件中每个kv扩展实现类
     * @param url
     * @param cl
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void loadClassViaConfigFile(Map<String, Class<?>> extensionClasses, URL url, ClassLoader cl) throws IOException, ClassNotFoundException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
        String line;
        // 遍历文件的每一行配置
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.length() > 0) {
                String name = "";
                String extClassName = "";
                int splitIndex = line.indexOf('=');
                if (splitIndex > 0) {
                    // 扩展名
                    name = line.substring(0, splitIndex).trim();
                    // 扩展实现类名
                    extClassName = line.substring(splitIndex + 1).trim();
                }
                if (name.length() > 0 && extClassName.length() > 0) {
                    // 加载这个扩展实现类
                    Class<?> extClass = Class.forName(extClassName, true, cl);
                    // 边界验证
                    if (!type.isAssignableFrom(extClass)) {
                        String errMsg = "Exception occurrs when loading extension class (interface: " + type + "), " +
                                "it is not supertype of class " + extClass.getName();
                        logger.error(errMsg);
                        throw new IllegalStateException(errMsg);
                    }
                    Class<?> clazz = extensionClasses.get(name);
                    // 最后将扩展名和扩展实现类的映射关系记录到 cachedClasses 缓存
                    if (clazz == null) {
                        extensionClasses.put(name, extClass);
                    } else {
                        String errMsg = "Duplicate extension " + type.getName();
                        logger.error(errMsg);
                        throw new IllegalStateException(errMsg);
                    }
                }
            }
        }
    }

    /**
     * 自动装配新建的实例对象/填充属性
     * @param instance
     */
//    private T injectExtensionInstance(T instance) {
//        // 扫描其全部 setter 方法
//        for (Method method : instance.getClass().getMethods()) {
//            if (!ClassUtils.isSetter(method)) {
//                continue;
//            }
//            try {
//                // 根据setter方法的参数，确定属性类型
//                Class<?> parameterType = method.getParameterTypes()[0];
//                // 根据setter方法的名称，确定属性名称
//                String property = ClassUtils.getSetterProperty(method);
//                // 加载并实例化setter参数的扩展实现类
//                Object object = extensionFactory.getExtension(parameterType);
//                // 调用相应的 setter 方法填充属性
//                if (object != null) {
//                    method.invoke(instance, object);
//                }
//            } catch (Throwable t) {
//                String errMsg = "Failed to inject via method " + method.getName() + " of interface " + type.getName();
//                logger.error(errMsg);
//                throw new IllegalStateException(errMsg, t);
//            }
//        }
//        return instance;
//    }
}
