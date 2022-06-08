package com.hatsukoi.genesis.remoting.transport.netty;

import com.hatsukoi.genesis.remoting.buffer.ChannelBuffer;
import com.hatsukoi.genesis.remoting.buffer.ChannelBufferFactory;
import io.netty.buffer.ByteBuf;

/**
 * @author gaoweilin
 * @date 2022/06/07 Tue 3:06 AM
 */
public class NettyChannelBuffer implements ChannelBuffer {

    private ByteBuf buffer;

    public NettyChannelBuffer(ByteBuf buffer) {
        this.buffer = buffer;
    }

    @Override
    public void getBytes(int index, ChannelBuffer dst) {

    }

    @Override
    public void setBytes(int index, ChannelBuffer src) {

    }

    @Override
    public void readBytes(ChannelBuffer dst, int length) {

    }

    @Override
    public void writeBytes(ChannelBuffer src, int length) {

    }

    @Override
    public void markReaderIndex() {

    }

    @Override
    public void markWriterIndex() {

    }

    @Override
    public void resetReaderIndex() {

    }

    @Override
    public void resetWriterIndex() {

    }

    @Override
    public int capacity() {
        return 0;
    }

    @Override
    public void clear() {

    }

    @Override
    public ChannelBuffer copy(int index, int length) {
        return null;
    }

    @Override
    public ChannelBufferFactory factory() {
        return null;
    }

    @Override
    public int compareTo(ChannelBuffer o) {
        return 0;
    }
}
