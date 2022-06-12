package com.hatsukoi.genesis.common.extension;

/**
 * @author gaoweilin
 * @date 2022/06/12 Sun 8:32 PM
 */
public class ExtensionFactory {
    public <T> T getExtension(Class<T> type) {
        if (type.isInterface() && type.isAnnotationPresent(SPI.class)) {
            ExtensionLoader<T> loader = ExtensionLoader.getExtensionLoader(type);
            return loader.getAdaptiveExtension();
        }
        return null;
    }
}
