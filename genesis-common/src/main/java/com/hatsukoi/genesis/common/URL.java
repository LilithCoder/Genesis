package com.hatsukoi.genesis.common;

import com.hatsukoi.genesis.common.utils.StringUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.hatsukoi.genesis.common.constant.CommonConstant.*;

/**
 * URL is used to describe all objects and config info
 * @author gaoweilin
 * @date 2022/06/03 Fri 7:28 PM
 */
public class URL implements Serializable {
    /**
     * RPC protocol
     */
    public String protocol;
    public String username;
    public String password;
    public String host;
    public int port;
    /**
     * full access path of this resource
     * Example:
     * com.hatsukoi.genesis.demoService
     */
    public String path;
    /**
     * key-value pair of the parameters
     */
    public Map<String, String> params;

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

    /**
     * Return param value based on specific key
     * @param key
     * @param defaultValue
     * @return
     */
    public String getParam(String key, String defaultValue) {
        String value = params.get(key);
        return StringUtils.isEmpty(value) ? defaultValue : value;
    }

    public String getServiceInterface() {
        return getParam(INTERFACE_KEY, path);
    }
}
