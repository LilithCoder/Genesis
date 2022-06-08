package com.hatsukoi.genesis.remoting;

import com.hatsukoi.genesis.common.URL;
import com.hatsukoi.genesis.common.extension.Adaptive;
import com.hatsukoi.genesis.common.extension.SPI;

import java.io.IOException;

import static com.hatsukoi.genesis.common.constant.remoting.RemotingConstant.*;

/**
 * @author gaoweilin
 * @date 2022/06/05 Sun 12:09 AM
 */
@SPI(value = "netty")
public interface Transporter {
    /**
     * 绑定一个server
     * @param url
     * @param handler
     * @return
     * @throws IOException
     */
    @Adaptive({SERVER_KEY, TRANSPORTER_KEY})
    Server bind(URL url, ChannelHandler handler) throws IOException;

    /**
     * 连接一个server
     */
    @Adaptive({CLIENT_KEY, TRANSPORTER_KEY})
    Client connect(URL url, ChannelHandler handler) throws IOException;
}
