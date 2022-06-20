package com.hatsukoi.genesis.remoting.buffer;

/**
 * @author gaoweilin
 * @date 2022/06/19 Sun 7:55 PM
 */
public class HeapChannelBuffer extends AbstractChannelBuffer{
    /**
     * The underlying heap byte array that this buffer is wrapping.
     */
    protected final byte[] array;

    public void setBytes(int index, byte[] src, int srcIndex, int length) {

        System.arraycopy(src, srcIndex, array, index, length);

    }

    public void getBytes(int index, byte[] dst, int dstIndex, int length) {

        System.arraycopy(array, index, dst, dstIndex, length);

    }

    @Override
    public ChannelBufferFactory factory() {
        return HeapChannelBufferFactory.getInstance();
    }
}
