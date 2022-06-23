package com.hatsukoi.genesis.compress;

/**
 * @author gaoweilin
 * @date 2022/06/21 Tue 4:18 AM
 */
public class CompressorFactory {
    public static Compressor get(byte extraInfo) {
        switch (extraInfo & 24) {
            case 0x0:
                return new SnappyCompressor();
            default:
                return new SnappyCompressor();
        }
    }
}