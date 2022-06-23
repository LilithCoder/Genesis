package com.hatsukoi.genesis.compress;

import java.io.IOException;

/**
 * 压缩算法
 * @author gaoweilin
 * @date 2022/06/21 Tue 4:17 AM
 */
public interface Compressor {
    byte[] compress(byte[] source) throws IOException;

    byte[] unCompress(byte[] source) throws IOException;
}
