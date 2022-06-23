package com.hatsukoi.genesis.serialization;

import java.io.IOException;

/**
 * 序列化接口
 * @author gaoweilin
 * @date 2022/06/21 Tue 4:06 AM
 */
public interface Serialization {
    <T> byte[] serialize(T obj) throws IOException;

    <T> T deserialize(byte[] data, Class<T> clazz) throws IOException;
}
