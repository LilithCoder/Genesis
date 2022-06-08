package com.hatsukoi.genesis.remoting.buffer;

/**
 * @author gaoweilin
 * @date 2022/06/07 Tue 3:00 AM
 */
public abstract class AbstractChannelBuffer implements ChannelBuffer {

    private int readerIndex;

    private int writerIndex;

    private int markedReaderIndex;

    private int markedWriterIndex;

    @Override
    public void readBytes(ChannelBuffer dst, int length) {
        // TODO
    }

    @Override
    public void writeBytes(ChannelBuffer src, int length) {
        // TODO
    }
}
