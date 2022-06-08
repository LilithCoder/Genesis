package com.hatsukoi.genesis.remoting.buffer;

/**
 * 抽象出 ChannelBuffer 接口对底层 NIO 框架中的 Buffer 设计进行统一
 * @author gaoweilin
 * @date 2022/06/07 Tue 1:35 AM
 */
public interface ChannelBuffer extends Comparable<ChannelBuffer> {

    /**
     * 从参数指定的位置读当前 ChannelBuffer，不会修改 readerIndex 的位置
     * @param index
     * @param dst
     */
    void getBytes(int index, ChannelBuffer dst);

    /**
     * 从参数指定的位置写当前 ChannelBuffer，不会修改 writerIndex 的位置
     * @param index
     * @param src
     */
    void setBytes(int index, ChannelBuffer src);

    /**
     * 读当前 ChannelBuffer，会从 readerIndex 指针开始读取数据，并移动 readerIndex 指针
     * @param dst
     * @param length
     */
    void readBytes(ChannelBuffer dst, int length);

    /**
     * 写当前 ChannelBuffer，会从 writerIndex 指针开始写入数据，并移动 writerIndex 指针
     * @param src
     * @param length
     */
    void writeBytes(ChannelBuffer src, int length);

    /**
     * 记录当前 readerIndex 指针的位置
     */
    void markReaderIndex();

    /**
     * 记录当前 writerIndex 指针的位置
     */
    void markWriterIndex();

    void resetReaderIndex();

    void resetWriterIndex();

    int capacity();

    void clear();

    ChannelBuffer copy(int index, int length);

    ChannelBufferFactory factory();

}
