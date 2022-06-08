package com.hatsukoi.genesis.common.extension;

import com.hatsukoi.genesis.common.utils.ClassUtils;
import com.hatsukoi.genesis.common.utils.StringUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Load genesis extension
 * @author gaoweilin
 * @date 2022/06/04 Sat 12:03 AM
 */
public class ExtensionLoader<T> {
    private static final String SPI_DIR = "META-INF/genesis/";
    private static final Logger logger = Logger.getLogger(ExtensionLoader.class);
    /**
     * The cached mapping of extension interface and ExtensionLoader instance
     * 缓存了扩展接口与加载其扩展实现的 ExtensionLoader 实例之间的映射关系
     */
    private static final ConcurrentMap<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>(64);
    /**
     * The cached mapping of extension class and extension instance
     * 缓存了扩展实现类与其实例对象的映射关系
     */
    private static final ConcurrentMap<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>(64);
    /**
     * Extension interface
     * 当前 ExtensionLoader 实例负责加载扩展接口
     */
    private final Class<?> type;
    /**
     * The cached mapping of extension name and Extension implementation instance
     * 缓存了该 ExtensionLoader 加载的扩展名与扩展实现对象之间的映射关系
     */
    private ConcurrentMap<String, Object> cachedInstances = new ConcurrentHashMap<>();
    /**
     * The cached mapping of extension name and Extension implementation class
     * 缓存了该 ExtensionLoader 加载的扩展名与扩展实现类之间的映射关系
     */
    private ConcurrentMap<String, Class<?>> cachedClasses = new ConcurrentHashMap<>();
    /**
     * The default extension name annotated by @SPI
     * 记录了 type 这个扩展接口上 @SPI 注解的 value 值，也就是默认扩展名
     */
    private String cachedDefaultName;

    private volatile Class<?> cachedAdaptiveClass;
    private Object cachedAdaptiveInstance;

    private ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    /**
     * Get extension loader corresponding to the extension interface from EXTENSION_LOADERS
     * 根据扩展接口从 EXTENSION_LOADERS 缓存中查找相应的 ExtensionLoader 实例
     * @param type
     * @param <T>
     * @return
     */
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Extension type == null");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension type (" + type + ") is not an interface");
        }
        if (!type.isAnnotationPresent(SPI.class)) {
            throw new IllegalArgumentException("Extension type (" + type + ") is not an extension interface, because " +
                    "it is NOT annotated with @" + SPI.class.getSimpleName());
        }
        ExtensionLoader<T> loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        if (loader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<T>(type));
            loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        }
        return loader;
    }

    /**
     * Find the extension instance from cachedInstances according to the extension name
     * 根据传入的扩展名称从 cachedInstances 缓存中查找扩展实现的实例
     * @param name
     * @return
     */
    public T getExtension(String name) throws InstantiationException, IllegalAccessException {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Extension name is null");
        }
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

    /**
     * 获取适配器实例，并将该实例缓存到 cachedAdaptiveInstance 字段
     * @return
     */
    public T getAdaptiveExtension() {
        if (cachedAdaptiveInstance == null) {
            synchronized (cachedAdaptiveInstance) {
                if (cachedAdaptiveInstance == null) {
                    try {
                        cachedAdaptiveInstance = createAdaptiveInstance();
                    } catch (Exception e) {
                        logger.error("Failed to create adaptive instance: ", e);
                    }
                }
            }
        }
        return (T) cachedAdaptiveInstance;
    }

    private Object createAdaptiveInstance() throws IllegalAccessException, InstantiationException {
        readConfigAndLoadExtClass();
        if (cachedAdaptiveClass != null) {
            return cachedAdaptiveClass.newInstance();
        } else {
            // TODO: dynamically generate adaptive class
        }
        return null;
    }

    /**
     * 完成了 SPI 配置文件的查找以及相应扩展实现类的实例化
     * @param name
     * @return
     */
    private T createExtension(String name) throws IllegalAccessException, InstantiationException {
        // Load extension class via name
        loadExtensionClass(name);
        Class<?> clazz = cachedClasses.get(name);
        if (clazz == null) {
            throw new RuntimeException("Cannot find the extension class according to name \"" + name +"\"");
        }
        // Instantiate the extension class via reflection
        T instance = (T) EXTENSION_INSTANCES.get(clazz);
        if (instance == null) {
            EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.newInstance());
            instance = (T) EXTENSION_INSTANCES.get(clazz);
        }
        // TODO: inject properties
        return instance;
    }

    private void loadExtensionClass(String name) {
        Class<?> cachedClass = cachedClasses.get(name);
        if (cachedClass == null) {
            synchronized (cachedClasses) {
                cachedClass = cachedClasses.get(name);
                if (cachedClass == null) {
                    // Get and cache the default extension name from SPI value
                    SPI defaultAnnotation = type.getAnnotation(SPI.class);
                    if (defaultAnnotation != null) {
                        String value = defaultAnnotation.value().trim();
                        cachedDefaultName = value;
                    }
                    readConfigAndLoadExtClass();
                }
            }
        }
    }

    /**
     * read SPI config files and load extension class
     */
    private void readConfigAndLoadExtClass() {
        // 扫描前面介绍的 SPI 目录获取查找相应的 SPI 配置文件，然后加载其中的扩展实现类，
        // 最后将扩展名和扩展实现类的映射关系记录到 cachedClasses 缓存
        String fileName = SPI_DIR + type;
        try {
            // Get classLoader first
            ClassLoader cl = ClassUtils.getClassLoader(ExtensionLoader.class);
            Enumeration<URL> resources = cl.getResources(fileName);

            // traverse all SPI config files
            if (resources != null) {
                while (resources.hasMoreElements()) {
                    URL url = resources.nextElement();
                    doLoadClass(url, cl);
                }
            }
        } catch (Exception e) {
            String errMsg = "Exception occurrs when loading extension class (interface: " + type + ") from file:"
                    + fileName;
            logger.error(errMsg, e);
        }
    }

    /**
     * cache the mapping of extension name and extension class
     * @param url
     * @param cl
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void doLoadClass(URL url, ClassLoader cl) throws IOException, ClassNotFoundException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.length() > 0) {
                String name = "";
                String extClassName = "";
                int splitIndex = line.indexOf('=');
                if (splitIndex > 0) {
                    name = line.substring(0, splitIndex).trim();
                    extClassName = line.substring(splitIndex + 1).trim();
                }
                if (name.length() > 0 && extClassName.length() > 0) {
                    Class<?> extClass = Class.forName(extClassName, true, cl);
                    if (!type.isAssignableFrom(extClass)) {
                        String errMsg = "Exception occurrs when loading extension class (interface: " + type + "), " +
                                "it is not supertype of class " + extClass.getName();
                        throw new IllegalStateException(errMsg);
                    }
                    // 识别加载扩展实现类上的 @Adaptive 注解，
                    // 将该扩展实现的类型缓存到 cachedAdaptiveClass 这个实例字段上（volatile修饰）
                    if (extClass.isAnnotationPresent(Adaptive.class)) {
                        if (cachedAdaptiveClass == null) {
                            cachedAdaptiveClass = extClass;
                        } else if (!cachedAdaptiveClass.equals(extClass)) {
                            throw new IllegalStateException("More than 1 adaptive class found: " +
                                    cachedAdaptiveClass.getName());
                        }
                    } else {
                        Class<?> clazz = cachedClasses.get(name);
                        if (clazz == null) {
                            cachedClasses.put(name, extClass);
                        } else {
                            throw new IllegalStateException("Duplicate extension");
                        }
                    }
                }
            }
        }
    }

    public T getDefaultExtension() {
        return null;
    }
}





























