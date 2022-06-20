package com.hatsukoi.genesis.remoting.buffer;

import java.nio.ByteBuffer;

/**
 * @author gaoweilin
 * @date 2022/06/07 Tue 3:11 AM
 */
public class ChannelBuffers {

    /**
     * 创建 DynamicChannelBuffer 对象，初始化大小由第一个参数指定，默认为 256
     * @param capacity
     * @return
     */
    public static ChannelBuffer dynamicBuffer(int capacity) {
        return new DynamicChannelBuffer(capacity);
    }

    /**
     * 创建指定大小的 HeapChannelBuffer 对象
     * @param capacity
     * @return
     */
    public static ChannelBuffer buffer(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity can not be negative");
        }
        if (capacity == 0) {
            return EMPTY_BUFFER;
        }
        return new HeapChannelBuffer(capacity);
    }

    /**
     * 将传入的 byte[] 数字封装成 HeapChannelBuffer 对象
     * @param buffer
     * @return
     */
    public static ChannelBuffer wrappedBuffer(ByteBuffer buffer) {
        if (!buffer.hasRemaining()) {
            return EMPTY_BUFFER;
        }
        if (buffer.hasArray()) {
            return wrappedBuffer(buffer.array(), buffer.arrayOffset() + buffer.position(), buffer.remaining());
        } else {
            return new ByteBufferBackedChannelBuffer(buffer);
        }
    }

    /**
     * 创建 ByteBufferBackedChannelBuffer 对象
     * @param capacity
     * @return
     */
    public static ChannelBuffer directBuffer(int capacity) {
        if (capacity == 0) {
            return EMPTY_BUFFER;
        }

        ChannelBuffer buffer = new ByteBufferBackedChannelBuffer(
                ByteBuffer.allocateDirect(capacity));
        buffer.clear();
        return buffer;
    }

    /**
     * 用于比较两个 ChannelBuffer 是否相同
     * @param bufferA
     * @param bufferB
     * @return
     */
    public static boolean equals(ChannelBuffer bufferA, ChannelBuffer bufferB) {
        final int aLen = bufferA.readableBytes();
        if (aLen != bufferB.readableBytes()) {
            return false;
        }

        final int byteCount = aLen & 7;

        int aIndex = bufferA.readerIndex();
        int bIndex = bufferB.readerIndex();

        for (int i = byteCount; i > 0; i--) {
            if (bufferA.getByte(aIndex) != bufferB.getByte(bIndex)) {
                return false;
            }
            aIndex++;
            bIndex++;
        }

        return true;
    }

    /**
     * 比较两个 ChannelBuffer 的大小
     * @param bufferA
     * @param bufferB
     * @return
     */
    public static int compare(ChannelBuffer bufferA, ChannelBuffer bufferB) {
        final int aLen = bufferA.readableBytes();
        final int bLen = bufferB.readableBytes();
        final int minLength = Math.min(aLen, bLen);

        int aIndex = bufferA.readerIndex();
        int bIndex = bufferB.readerIndex();

        for (int i = minLength; i > 0; i--) {
            byte va = bufferA.getByte(aIndex);
            byte vb = bufferB.getByte(bIndex);
            if (va > vb) {
                return 1;
            } else if (va < vb) {
                return -1;
            }
            aIndex++;
            bIndex++;
        }

        return aLen - bLen;
    }
}
