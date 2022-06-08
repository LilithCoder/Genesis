package com.hatsukoi.genesis.serialize;

import java.io.IOException;

/**
 * @author gaoweilin
 * @date 2022/06/06 Mon 2:53 AM
 */
public interface DataOutput {
    void writeObject(Object data) throws IOException;
    void writeBytes(byte[] data) throws IOException;
}
