package com.hatsukoi.genesis.remoting;

import com.hatsukoi.genesis.common.URL;
import com.hatsukoi.genesis.common.extension.Adaptive;
import com.hatsukoi.genesis.common.extension.SPI;

import java.io.IOException;

import static com.hatsukoi.genesis.common.constant.remoting.RemotingConstant.*;

/**
 * 有传输能力的抽象
 * 可以通过 SPI 修改使用的具体 Transporter 扩展实现，
 * 从而切换到不同的 Client 和 RemotingServer 实现，达到底层 NIO 库切换的目的
 * 符合开放-封闭原则
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
