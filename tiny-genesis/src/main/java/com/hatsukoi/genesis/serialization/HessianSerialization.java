package com.hatsukoi.genesis.serialization;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author gaoweilin
 * @date 2022/06/21 Tue 4:08 AM
 */
public class HessianSerialization implements Serialization {

    /**
     * 序列化
     * @param obj
     * @param <T>
     * @return
     * @throws IOException
     */
    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Hessian2Output output = new Hessian2Output(outputStream);
        output.writeObject(obj);
        output.flush();
        return outputStream.toByteArray();
    }

    /**
     * 反序列化
     * @param data
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        Hessian2Input input = new Hessian2Input(inputStream);
        return (T) input.readObject(clazz);
    }
}
