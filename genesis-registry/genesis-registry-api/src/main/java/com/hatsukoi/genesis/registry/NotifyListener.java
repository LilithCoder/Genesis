package com.hatsukoi.genesis.rpc;

import com.hatsukoi.genesis.common.URL;

import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/06/05 Sun 1:41 AM
 */
public interface NotifyListener {
    /**
     * receive the notification when subscribed data has been changed
     * @param urls The list of registered information
     */
    void notify(List<URL> urls);
}
