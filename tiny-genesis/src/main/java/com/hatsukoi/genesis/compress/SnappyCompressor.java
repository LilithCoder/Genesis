package com.hatsukoi.genesis.compress;

import org.xerial.snappy.Snappy;

import java.io.IOException;

/**
 * @author gaoweilin
 * @date 2022/06/21 Tue 4:19 AM
 */
public class SnappyCompressor implements Compressor {

    @Override
    public byte[] compress(byte[] source) throws IOException {
        if (source == null) {
            return null;
        }
        return Snappy.compress(source);
    }

    @Override
    public byte[] unCompress(byte[] source) throws IOException {
        if (source == null) {
            return null;
        }
        return Snappy.uncompress(source);
    }
}
