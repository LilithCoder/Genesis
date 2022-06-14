package com.hatsukoi.genesis.registry;

import com.hatsukoi.genesis.common.URL;
import com.hatsukoi.genesis.common.constant.RegistryConstant;
import com.hatsukoi.genesis.common.utils.CollectionUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static com.hatsukoi.genesis.common.constant.RegistryConstant.CATEGORY_KEY;
import static com.hatsukoi.genesis.common.constant.RegistryConstant.PROVIDERS_CATEGORY;
import static com.hatsukoi.genesis.common.utils.UrlUtils.isMatch;

/**
 * @author gaoweilin
 * @date 2022/06/05 Sun 10:18 AM
 */
public abstract class AbstractRegistry implements Registry {
    // ===================== 静态变量 =====================
    private static final Logger logger = Logger.getLogger(AbstractRegistry.class);

    // ===================== 成员变量 =====================
    /**
     * 该 URL 包含了创建该 Registry 对象的全部配置信息，是 AbstractRegistryFactory 修改后的产物 「详情可见getRegistry()」
     */
    private URL registryUrl;
    /**
     * 本地的 Properties 文件缓存, 加载到内存的 Properties 对象，和磁盘上对应的文件数据同步
     * 特殊 kv：key 为 registies，value 为注册中心列表
     * 其余 kv：key 为 当前节点作为 Consumer 的一个 URL，value 为对应的 Provider 列表
     */
    private final Properties properties = new Properties();
    /**
     * 磁盘上对应的文件
     */
    private File file;
    /**
     * 是否同步保存文件的配置，对应的是 registryUrl 中的 save.file 参数
     */
    private boolean syncSaveFile;
    private final ExecutorService registryCacheExecutor = new ThreadPoolExecutor(20,
            200,
            10,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(10000),
            new ThreadPoolExecutor.AbortPolicy());
    /**
     * 注册的 URL 集合
     */
    private final Set<URL> registered = ConcurrentHashMap.newKeySet();
    /**
     * 订阅 URL 的监听器集合，其中 Key 是被监听的 URL， Value 是相应的监听器集合
     */
    private final ConcurrentMap<URL, Set<NotifyListener>> subscribed = new ConcurrentHashMap<>();
    /**
     * 第一层 Key 是当前节点作为 Consumer 的一个 服务接口，表示的是该节点的某个 Consumer 角色
     * Value 是一个 Map 集合
     * 该 Map 集合的 Key 是 Provider URL 的分类（Category），例如 providers、routes、configurators 等
     * Value 就是相应分类下的 URL 集合
     */
    private final ConcurrentMap<URL, Map<String, List<URL>>> notified = new ConcurrentHashMap<>();
    /**
     * 注册数据的版本号
     * 每次写入 file 文件时，都是全覆盖写入，而不是修改文件，所以需要版本控制，防止旧数据覆盖新数据
     */
    private final AtomicLong lastCacheChanged = new AtomicLong();

    // ===================== 构造函数 =====================

    public AbstractRegistry(URL url) {
        // 将本地缓存文件加载到 properties 对象中
        loadProperties();
    }

    // ===================== 公开方法 =====================
    /**
     * 来自 Provider 的通知
     * 当 Provider 端暴露的 URL 发生变化时，ZooKeeper 等服务发现组件会通知 Consumer 端的 Registry 组件，
     * Registry 组件会调用 notify() 方法，被通知的 Consumer 能匹配到所有 Provider 的 URL 列表并写入 properties 集合中
     * @param consumer      consumer url
     * @param listener      consumer 对应的监听器
     * @param urls          匹配到所有 Provider 的 URL 列表
     */
    protected void notify(URL consumer, NotifyListener listener, List<URL> urls) {
        if (consumer == null) {
            throw new IllegalArgumentException("notify consumer url == null");
        }
        if (listener == null) {
            throw new IllegalArgumentException("notify listener == null");
        }
        if (CollectionUtils.isEmpty(urls)) {
            logger.warn("Ignore empty notify urls for consumer (" + consumer + ")");
            return;
        }
        // 缓存Consumer订阅的各个分类的URL
        logger.info("Notify consumer ("+ consumer +") for change from provider ("+ urls +")");
        // key 为 Provider 分类， value 为 相应分类下的 URL 集合
        Map<String, List<URL>> providers = new HashMap<>();
        // 遍历每个 Provider 的 url
        for (URL provider : urls) {
            // TODO: match consumer url and provider url
            if (isMatch(consumer, provider)) {
                // 获取这个 Provider 的分类 「providers, configurators, routers」
                String category = provider.getParam(CATEGORY_KEY, PROVIDERS_CATEGORY);
                List<URL> categoryList = providers.computeIfAbsent(category, k -> new ArrayList<>());
                categoryList.add(provider);
            }
        }
        if (providers.size() == 0) {
            logger.warn("No providers needs to be notified");
            return;
        }
        Map<String, List<URL>> categoryNotified = notified.computeIfAbsent(consumer, k -> new ConcurrentHashMap<>());
        for (Map.Entry<String, List<URL>> entry : providers.entrySet()) {
            String category = entry.getKey();
            List<URL> providerList = entry.getValue();
            categoryNotified.put(category, providerList);
            // invoke notifyListener
            listener.notify(providerList);
            // TODO: file cache 通过本地缓存提供了一种容错机制，保证了服务的可靠性
            saveProperties(consumer);
        }
        notified.put(consumer, categoryNotified);
    }

    /**
     * 每次通知都缓存到磁盘中
     * @param consumer
     */
    private void saveProperties(URL consumer) {
        if (file == null) return;
        try {
            // 1. 取出 Consumer 订阅的各个分类的 URL 连接起来（中间以空格分隔）
            StringBuilder buf = new StringBuilder();
            Map<String, List<URL>> categoryNotified = notified.get(consumer);
            if (categoryNotified != null) {
                for (List<URL> us : categoryNotified.values()) {
                    for (URL u : us) {
                        if (buf.length() > 0) {
                            buf.append(" ");
                        }
                        buf.append(u.toString());
                    }
                }
            }
            // 2. 以 Consumer 的 服务接口为键值写到 properties 中
            properties.setProperty(consumer.getServiceKey(), buf.toString());
            // 3. 同时 lastCacheChanged 版本号会自增
            long version = lastCacheChanged.incrementAndGet();
            // 4. 根据 syncSaveFile 字段值来决定更新文件
            if (syncSaveFile) {
                // 在当前线程同步更新 file 文件
                doSaveProperties(version);
            } else {
                // 向 registryCacheExecutor 线程池提交任务，异步完成 file 文件的同步
                registryCacheExecutor.execute(new SaveProperties(version));
            }
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
        }
    }

    /**
     * 本地缓存文件
     * @param version
     */
    public void doSaveProperties(long version) {
        // TODO:
    }

    /**
     * 本地缓存文件线程池任务
     */
    private class SaveProperties implements Runnable {
        private long version;

        private SaveProperties(long version) {
            this.version = version;
        }

        @Override
        public void run() {
            doSaveProperties(version);
        }
    }

    /**
     * 将本地缓存文件加载到 properties 对象中
     */
    private void loadProperties() {
        if (file != null && file.exists()) {
            InputStream in = null;
            try {
                in = new FileInputStream(file);
                properties.load(in);
                if (logger.isInfoEnabled()) {
                    logger.info("Load registry cache file " + file + ", data: " + properties);
                }
            } catch (Throwable e) {
                logger.warn("Failed to load registry cache file " + file, e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        logger.warn(e.getMessage(), e);
                    }
                }
            }
        }
    }

    /**
     * 将当前节点要注册的 URL 缓存到 registered 集合
     * @param url
     */
    @Override
    public void register(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("register url is null");
        }
        registered.add(url);
    }

    /**
     * 从 registered 集合删除指定的 URL
     * @param url
     */
    @Override
    public void unregister(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("unregister url is null");
        }
        registered.remove(url);
    }

    /**
     *
     * @param url
     * @param listener      订阅数据的监听器
     */
    @Override
    public void subscribe(URL url, NotifyListener listener) {
        if (url == null) {
            throw new IllegalArgumentException("subscribe url is null");
        }
        if (listener == null) {
            throw new IllegalArgumentException("subscribe listener is null");
        }
        Set<NotifyListener> listeners = subscribed.computeIfAbsent(url, k -> ConcurrentHashMap.newKeySet());
        listeners.add(listener);
    }

    /**
     * 将当前节点的 URL 以及关联的 NotifyListener 从 subscribed 集合删除
     * @param url
     * @param listener      订阅数据的监听器
     */
    @Override
    public void unsubscribe(URL url, NotifyListener listener) {
        if (url == null) {
            throw new IllegalArgumentException("unsubscribe url is null");
        }
        if (listener == null) {
            throw new IllegalArgumentException("unsubscribe listener is null");
        }
        Set<NotifyListener> listeners = subscribed.get(url);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    /**
     * 恢复
     */
    protected void recover() {
        // 将 registered 集合中的全部 URL 重新走一遍 register() 方法，恢复注册数据
        Set<URL> recoverRegistered = Collections.unmodifiableSet(registered);
        if (!recoverRegistered.isEmpty()) {
            for (URL url : recoverRegistered) {
                register(url);
            }
        }
        // 将 subscribed 集合中的 URL 重新走一遍 subscribe() 方法，恢复订阅监听器
        Map<URL, Set<NotifyListener>> recoverSubscribed = Collections.unmodifiableMap(subscribed);
        if (!recoverSubscribed.isEmpty()) {
            for (Map.Entry<URL, Set<NotifyListener>> entry : recoverSubscribed.entrySet()) {
                URL url = entry.getKey();
                for (NotifyListener listener : entry.getValue()) {
                    subscribe(url, listener);
                }
            }
        }
    }

    /**
     * 销毁
     */
    @Override
    public void destory() {
        // unregister
        Set<URL> destoryRegistered = Collections.unmodifiableSet(registered);
        if (!destoryRegistered.isEmpty()) {
            for (URL url : destoryRegistered) {
                unregister(url);
            }
        }
        // unsubscribe
        Map<URL, Set<NotifyListener>> destorySubscribed = Collections.unmodifiableMap(subscribed);
        if (!destorySubscribed.isEmpty()) {
            for (Map.Entry<URL, Set<NotifyListener>> entry : destorySubscribed.entrySet()) {
                URL url = entry.getKey();
                for (NotifyListener listener : entry.getValue()) {
                    unsubscribe(url, listener);
                }
            }
        }
    }
}
