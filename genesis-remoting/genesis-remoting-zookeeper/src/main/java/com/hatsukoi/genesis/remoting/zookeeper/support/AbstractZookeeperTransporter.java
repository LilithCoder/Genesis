package com.hatsukoi.genesis.remoting.zookeeper.support;

import com.hatsukoi.genesis.common.URL;
import com.hatsukoi.genesis.common.utils.StringUtils;
import com.hatsukoi.genesis.remoting.zookeeper.ZookeeperClient;
import com.hatsukoi.genesis.remoting.zookeeper.ZookeeperTransporter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存 ZookeeperClient 实例
 * 在某个 Zookeeper 节点无法连接时，切换到备用 Zookeeper 地址
 * 在配置 Zookeeper 地址的时候，我们可以配置多个 Zookeeper 节点的地址，
 * 这样的话，当一个 Zookeeper 节点宕机之后，Dubbo 就可以主动切换到其他 Zookeeper 节点
 * @author gaoweilin
 * @date 2022/06/19 Sun 12:27 PM
 */
public abstract class AbstractZookeeperTransporter implements ZookeeperTransporter {
    private final Map<String, ZookeeperClient> zookeeperClientMap = new ConcurrentHashMap<>();

    protected abstract ZookeeperClient createZookeeperClient(URL url);

    /**
     * 缓存 ZookeeperClient 实例
     * 在某个 Zookeeper 节点无法连接时，切换到备用 Zookeeper 地址
     * @param url       zookeeper://127.0.0.1:2181/org.apache.dubbo.registry.RegistryService?backup=127.0.0.1:8989,127.0.0.1:9999
     * @return
     */
    @Override
    public ZookeeperClient connect(URL url) {
        ZookeeperClient zookeeperClient;
        // 获取备用 Zookeeper 地址
        // 得到上述 URL 中配置的 127.0.0.1:2181、127.0.0.1:8989 和 127.0.0.1:9999 这三个 Zookeeper 节点地址
        List<String> addressList = getURLBackupAddress(url);
        // 从缓存中获取连接的zookeeper
        // 从 ZookeeperClientMap 缓存
        // （这是一个 Map，Key 为 Zookeeper 节点地址，Value 是相应的 ZookeeperClient 实例）中查找一个可用 ZookeeperClient 实例
        if ((zookeeperClient = fetchAndUpdateZookeeperClientCache(addressList)) != null && zookeeperClient.isConnected()) {
            logger.info("find valid zookeeper client from the cache for address: " + url);
            // 如果查找成功，则复用 ZookeeperClient 实例
            return zookeeperClient;
        }
        synchronized (zookeeperClientMap) {
            if ((zookeeperClient = fetchAndUpdateZookeeperClientCache(addressList)) != null && zookeeperClient.isConnected()) {
                logger.info("find valid zookeeper client from the cache for address: " + url);
                return zookeeperClient;
            }
            // 查找失败，则创建一个新的 ZookeeperClient 实例返回并更新 ZookeeperClientMap 缓存
            zookeeperClient = createZookeeperClient(url);
            writeToClientMap(addressList, zookeeperClient);
        }
        return zookeeperClient;
    }

    /**
     * {[username:password@]address}
     * @param url       zookeeper://127.0.0.1:2181/org.apache.dubbo.registry.RegistryService?backup=127.0.0.1:8989,127.0.0.1:9999
     * @return
     */
    private List<String> getURLBackupAddress(URL url) {
        List<String> addressList = new ArrayList<String>();
//        addressList.add(url.getAddress());
//        addressList.addAll(url.getParam("backup", new ArrayList<>()));

        String authPrefix = null;
        if (StringUtils.isNotEmpty(url.getUsername())) {
            StringBuilder buf = new StringBuilder();
            buf.append(url.getUsername());
            if (StringUtils.isNotEmpty(url.getPassword())) {
                buf.append(":");
                buf.append(url.getPassword());
            }
            buf.append("@");
            authPrefix = buf.toString();
        }

        if (StringUtils.isNotEmpty(authPrefix)) {
            List<String> authedAddressList = new ArrayList<>(addressList.size());
            for (String addr : addressList) {
                authedAddressList.add(authPrefix + addr);
            }
            return authedAddressList;
        }


        return addressList;
    }
}
