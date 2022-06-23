package com.hatsukoi.genesis.codec;

import com.hatsukoi.genesis.Utils;
import com.hatsukoi.genesis.compress.Compressor;
import com.hatsukoi.genesis.compress.CompressorFactory;
import com.hatsukoi.genesis.protocol.Header;
import com.hatsukoi.genesis.protocol.Message;
import com.hatsukoi.genesis.serialization.Serialization;
import com.hatsukoi.genesis.serialization.SerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author gaoweilin
 * @date 2022/06/21 Tue 11:04 PM
 */
public class RpcEncoder extends MessageToByteEncoder<Message> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {
        Header header = message.getHeader();
        // 依次序列化消息头中的魔数、版本、基本信息以及消息ID
        byteBuf.writeShort(header.getMagic());
        byteBuf.writeByte(header.getVersion());
        byteBuf.writeByte(header.getBaseInfo());
        byteBuf.writeLong(header.getMessageId());
        Object content = message.getContent();
        if (Utils.isHeartBeat(header.getBaseInfo())) {
            // 心跳消息，没有消息体，这里写入0
            byteBuf.writeInt(0);
        }
        // 按照baseInfo部分指定的序列化方式和压缩方式进行处理
        Serialization serialization = SerializationFactory.get(header.getBaseInfo());
        Compressor compressor = CompressorFactory.get(header.getBaseInfo());
        byte[] payload = compressor.compress(serialization.serialize(content));
        // 写入消息体长度
        byteBuf.writeInt(payload.length);
        // 写入消息体内容
        byteBuf.writeBytes(payload);
    }
}
