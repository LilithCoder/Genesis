package com.hatsukoi.genesis.remoting.transport.codec;

import com.hatsukoi.genesis.remoting.Channel;
import com.hatsukoi.genesis.remoting.Codec;
import com.hatsukoi.genesis.remoting.buffer.ChannelBuffer;

import java.io.IOException;

/**
 * @author gaoweilin
 * @date 2022/06/19 Sun 8:54 PM
 */
public class CodecAdapter implements Codec {
    private Codec codec;

    public CodecAdapter(Codec codec) {
        Assert.notNull(codec, "codec == null");
        this.codec = codec;
    }

    @Override
    public void encode(Channel channel, ChannelBuffer buffer, Object message)
            throws IOException {
        UnsafeByteArrayOutputStream os = new UnsafeByteArrayOutputStream(1024);
        codec.encode(channel, os, message);
        buffer.writeBytes(os.toByteArray());
    }

    @Override
    public Object decode(Channel channel, ChannelBuffer buffer) throws IOException {
        byte[] bytes = new byte[buffer.readableBytes()];
        int savedReaderIndex = buffer.readerIndex();
        buffer.readBytes(bytes);
        UnsafeByteArrayInputStream is = new UnsafeByteArrayInputStream(bytes);
        Object result = codec.decode(channel, is);
        buffer.readerIndex(savedReaderIndex + is.position());
        return result == Codec.NEED_MORE_INPUT ? DecodeResult.NEED_MORE_INPUT : result;
    }

    public Codec getCodec() {
        return codec;
    }
}
