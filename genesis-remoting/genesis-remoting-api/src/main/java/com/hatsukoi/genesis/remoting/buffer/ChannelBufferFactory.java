package com.hatsukoi.genesis.remoting.buffer;

/**
 * @author gaoweilin
 * @date 2022/06/07 Tue 2:59 AM
 */
public interface ChannelBufferFactory {
    ChannelBuffer getBuffer(int capacity);
}
