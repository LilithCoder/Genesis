package com.hatsukoi.genesis.remoting.buffer;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author gaoweilin
 * @date 2022/06/07 Tue 3:09 AM
 */
public class ChannelBufferInputStream extends InputStream {

    private final ChannelBuffer buffer;

    public ChannelBufferInputStream(ChannelBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public int read() throws IOException {
        return 0;
    }
}
