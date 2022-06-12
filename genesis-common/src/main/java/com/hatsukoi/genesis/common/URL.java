package com.hatsukoi.genesis.common;

import com.hatsukoi.genesis.common.utils.StringUtils;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.hatsukoi.genesis.common.constant.CommonConstant.*;

/**
 * URL is used to describe all objects and config info
 * URL 来统一描述了所有对象和配置信息 (统一配置模型)
 * Example: Provider注册到注册中心的URL信息
 * genesis://127.0.0.1:20880/com.hatsukoi.genesis.demo.ProviderService?anyhost=true&
 * application=dubbo-demo-annotation-provider&genesis=1.0.0&interface=com.hatsukoi.genesis.demo.ProviderService&
 * methods=hello,helloAsync&pid=32508&release=&side=provider&timestamp=1593253404714
 * @author gaoweilin
 * @date 2022/06/03 Fri 7:28 PM
 */
public class URL implements Serializable {
    private String protocol;                // genesis
    private String username;
    private String password;
    private String host;                    // 127.0.0.1
    private int port;                       // 8080
    private String path;                    // com.hatsukoi.genesis.demo.ProviderService
    private String address;                 // 127.0.0.1:8080
    private Map<String, String> params;     // 参数键值对

    // ================== constructor ==================
    public URL(String protocol,
               String username,
               String password,
               String host,
               int port,
               String path,
               Map<String, String> params) {
        if (StringUtils.isEmpty(username) && StringUtils.isEmpty(password)) {
            throw new IllegalArgumentException("Invalid URL, both username and password are empty");
        }
        this.protocol = protocol;
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = Math.max(port, 0);
        this.address = getAddress(this.host, this.port);
        // trim the beginning "/"
        while (path != null && path.startsWith("/")) {
            path = path.substring(1);
        }
        this.path = path;
        if (params == null) {
            params = new HashMap<>();
        } else {
            params = new HashMap<>(params);
        }
        this.params = Collections.unmodifiableMap(params);
    }

    // ================== private methods ==================
    /**
     * 获取地址
     * @param host
     * @param port
     * @return
     */
    private String getAddress(String host, int port) {
        return port <= 0 ? host : host + ':' + port;
    }

    public String getParam(String key, String defaultValue) {
        String value = params.get(key);
        return StringUtils.isEmpty(value) ? defaultValue : value;
    }

    public int getParam(String key, int defaultValue) {
        String value = params.get(key);
        return StringUtils.isEmpty(value) ? defaultValue : Integer.parseInt(value);
    }

    public String getServiceInterface() {
        return getParam(INTERFACE_KEY, path);
    }

    public InetSocketAddress toInetSocketAddress() {
        return new InetSocketAddress(host, port);
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public String getProtocol() {
        return this.protocol;
    }

}
