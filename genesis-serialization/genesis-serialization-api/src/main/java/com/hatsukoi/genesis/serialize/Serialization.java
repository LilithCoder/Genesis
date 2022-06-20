package com.hatsukoi.genesis.serialize;

import com.hatsukoi.genesis.common.URL;
import com.hatsukoi.genesis.common.extension.Adaptive;
import com.hatsukoi.genesis.common.extension.SPI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Serialization strategy
 * The default extension is hessian2
 * @author gaoweilin
 * @date 2022/06/06 Mon 1:38 AM
 */
@SPI("hessian2") // 被@SPI注解修饰，默认是使用hessian2序列化算法
public interface Serialization {

    // 每一种序列化算法都对应一个ContentType，该方法用于获取ContentType
    String getContentType();

    // 获取ContentType的ID值，是一个byte类型的值，唯一确定一个算法
    byte getContentTypeId();

    /**
     * 创建一个ObjectOutput对象，ObjectOutput负责实现序列化的功能
     * Java对象转化为字节序列
     * @param url
     * @param output
     * @return
     * @throws IOException
     */
    @Adaptive
    DataOutput serialize(URL url, OutputStream output) throws IOException;

    /**
     * 创建一个ObjectInput对象，ObjectInput负责实现反序列化的功能
     * 将字节序列转换成Java对象
     * @param url
     * @param input
     * @return
     * @throws IOException
     */
    @Adaptive
    DataInput deserialize(URL url, InputStream input) throws IOException;
}
