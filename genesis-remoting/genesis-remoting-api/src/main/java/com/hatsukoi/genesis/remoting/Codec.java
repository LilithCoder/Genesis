package com.hatsukoi.genesis.remoting;

import com.hatsukoi.genesis.common.extension.Adaptive;
import com.hatsukoi.genesis.common.extension.SPI;
import com.hatsukoi.genesis.remoting.buffer.ChannelBuffer;

import java.io.IOException;

import static com.hatsukoi.genesis.common.constant.remoting.RemotingConstant.CODEC_KEY;

/**
 * 负责编解码的 ChannelHandler 的抽象接口
 * @author gaoweilin
 * @date 2022/06/07 Tue 1:23 AM
 */
@SPI
public interface Codec {
    @Adaptive({CODEC_KEY})
    void encode(Channel channel, ChannelBuffer buffer, Object message) throws IOException;

    @Adaptive({CODEC_KEY})
    Object decode(Channel channel, ChannelBuffer buffer) throws IOException;
}
