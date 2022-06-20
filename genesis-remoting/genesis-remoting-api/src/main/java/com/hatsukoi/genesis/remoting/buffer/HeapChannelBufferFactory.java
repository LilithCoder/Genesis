package com.hatsukoi.genesis.remoting.buffer;

/**
 * @author gaoweilin
 * @date 2022/06/19 Sun 7:56 PM
 */
public class HeapChannelBufferFactory extends ChannelBufferFactory {
    @Override

    public ChannelBuffer getBuffer(int capacity) {

        // 新建一个HeapChannelBuffer，底层的会新建一个长度为capacity的byte数组

        return ChannelBuffers.buffer(capacity);

    }

    @Override

    public ChannelBuffer getBuffer(byte[] array, int offset, int length) {

        // 新建一个HeapChannelBuffer，并且会拷贝array数组中offset~offset+lenght

        // 的数据到新HeapChannelBuffer中

        return ChannelBuffers.wrappedBuffer(array, offset, length);

    }
}
