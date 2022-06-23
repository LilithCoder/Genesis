package com.hatsukoi.genesis.serialization;

/**
 * 序列化实现工厂
 * @author gaoweilin
 * @date 2022/06/21 Tue 4:14 AM
 */
public class SerializationFactory {

    public static Serialization get(byte type) {
        switch (type & 0x7) {
            case 0x0:
                return new HessianSerialization();
            default:
                return new HessianSerialization();
        }
    }
}
