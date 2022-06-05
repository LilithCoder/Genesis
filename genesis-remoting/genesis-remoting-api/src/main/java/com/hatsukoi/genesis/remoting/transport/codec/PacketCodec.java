package com.hatsukoi.genesis.codec;

/**
 *
 * @author gaoweilin
 * @date 2022/06/01 Wed 2:14 AM
 */
public class PacketCodec {
    /**
     * magic number - used to identify whether the packet conforms to our protocol
     * 魔数 - 用来鉴别是否接收到的数据包遵循我们的协议
     */
    private static final short MAGIC_NUMBER = 0x1234;
    /**
     * 版本号
     */
    private static final byte version = 1;
    /**
     * 0 - Response, 1 - Request
     * 数据包类型
     */
    private byte packetType;
    /**
     * 序列化算法
     */
    private byte serializationAlgo;

    public Object encode() {
        return null;
    }

    public Object decode() {
        return null;

    }
}
