package com.hatsukoi.genesis.serialize;

import java.io.IOException;

/**
 * @author gaoweilin
 * @date 2022/06/06 Mon 2:53 AM
 */
public interface DataInput {
    Object readObject() throws IOException;
    byte[] readBytes() throws IOException;
}
