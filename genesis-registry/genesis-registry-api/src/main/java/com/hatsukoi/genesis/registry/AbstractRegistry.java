package com.hatsukoi.genesis.registry;

import com.hatsukoi.genesis.common.URL;
import com.hatsukoi.genesis.common.constant.RegistryConstant;
import com.hatsukoi.genesis.common.utils.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.hatsukoi.genesis.common.constant.RegistryConstant.CATEGORY_KEY;
import static com.hatsukoi.genesis.common.constant.RegistryConstant.PROVIDERS_CATEGORY;
import static com.hatsukoi.genesis.common.utils.UrlUtils.isMatch;

/**
 * @author gaoweilin
 * @date 2022/06/05 Sun 10:18 AM
 */
public abstract class AbstractRegistry implements Registry {
    private static final Logger logger = Logger.getLogger(AbstractRegistry.class);
    private URL registryUrl;
    private final ConcurrentMap<URL, Map<String, List<URL>>> notified = new ConcurrentHashMap<>();
    private final Set<URL> registered = ConcurrentHashMap.newKeySet();
    private final ConcurrentMap<URL, Set<NotifyListener>> subscribed = new ConcurrentHashMap<>();

    public AbstractRegistry(URL url) {
        // TODO
    }

    /**
     * Notify changes from provider
     * @param consumer       consumer url
     * @param listener  listener
     * @param urls      provider urls
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
        // 缓存Cosumer订阅的各个分类的URL
        logger.info("Notify consumer ("+ consumer +") for change from provider ("+ urls +")");
        Map<String, List<URL>> providers = new HashMap<>();
        // Categorize providers based on 'category' parameter in URL
        for (URL provider : urls) {
            // TODO: match consumer url and provider url
            if (isMatch(consumer, provider)) {
                // indicate category to be subscribed, example: providers, configurators, routers
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

    protected void recover() {
        // register
        Set<URL> recoverRegistered = Collections.unmodifiableSet(registered);
        if (!recoverRegistered.isEmpty()) {
            for (URL url : recoverRegistered) {
                register(url);
            }
        }
        // subscribe
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
