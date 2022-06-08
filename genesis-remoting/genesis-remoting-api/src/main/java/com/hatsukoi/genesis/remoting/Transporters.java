package com.hatsukoi.genesis.remoting;

import com.hatsukoi.genesis.common.URL;
import com.hatsukoi.genesis.common.extension.ExtensionLoader;

/**
 * Transporter facade
 * @author gaoweilin
 * @date 2022/06/07 Tue 2:38 AM
 */
public class Transporters {

    public static Transporter getTransporter() {
        return ExtensionLoader.getExtensionLoader(Transporter.class).getAdaptiveExtension();
    }

    public static Server bind(URL url, ChannelHandler... handlers) {
        return null;
    }

    public static Client connect(URL url, ChannelHandler... handlers) {
        return null;
    }

}
