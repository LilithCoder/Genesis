package com.hatsukoi.genesis;

/**
 * @author gaoweilin
 * @date 2022/06/21 Tue 4:29 AM
 */
public class Constants {
    public static final short MAGIC = (short) 0xE0F1;
    public static final int HEADER_SIZE = 16;
    public static final byte VERSION_1 = 1;
    public static final int HEARTBEAT_CODE = -1;
    public static final int DEFAULT_IO_THREADS = Math.min(Runtime.getRuntime().availableProcessors() + 1, 32);
    public static final int DEFAULT_TIMEOUT = 500000;
}
