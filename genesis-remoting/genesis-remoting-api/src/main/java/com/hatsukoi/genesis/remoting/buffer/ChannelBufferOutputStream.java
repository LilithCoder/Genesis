package com.hatsukoi.genesis.remoting.buffer;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author gaoweilin
 * @date 2022/06/07 Tue 3:09 AM
 */
public class ChannelBufferOutputStream extends OutputStream {

    private final ChannelBuffer buffer;

    public ChannelBufferOutputStream(ChannelBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void write(int b) throws IOException {

    }
}
