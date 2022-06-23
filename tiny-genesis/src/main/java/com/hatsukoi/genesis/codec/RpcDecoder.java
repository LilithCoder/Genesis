package com.hatsukoi.genesis.codec;

import com.hatsukoi.genesis.Constants;
import com.hatsukoi.genesis.Utils;
import com.hatsukoi.genesis.compress.Compressor;
import com.hatsukoi.genesis.compress.CompressorFactory;
import com.hatsukoi.genesis.protocol.Header;
import com.hatsukoi.genesis.protocol.Message;
import com.hatsukoi.genesis.protocol.Request;
import com.hatsukoi.genesis.protocol.Response;
import com.hatsukoi.genesis.serialization.Serialization;
import com.hatsukoi.genesis.serialization.SerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 解码 ChannelHandler
 * @author gaoweilin
 * @date 2022/06/21 Tue 4:24 AM
 */
public class RpcDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
        // 1. 如果可读数据不到16字节，那么消息头就无法解析，直接返回
        if (byteBuf.readableBytes() < Constants.HEADER_SIZE) {
            return;
        }
        // 2. 记录读指针
        byteBuf.markReaderIndex();
        // 3. 魔数校验，不通过则充值读指针并抛异常
        short magic = byteBuf.readShort();
        if (magic != Constants.MAGIC) {
            byteBuf.resetReaderIndex();
            throw new RuntimeException("magic number error");
        }
        // 4. 读取协议版本、基本信息、消息id、消息体长度
        byte version = byteBuf.readByte();
        byte baseInfo = byteBuf.readByte();
        long messageId = byteBuf.readLong();
        int size = byteBuf.readInt();

        // 5. 获取、反序化、解压消息体的数据
        Object body = null;
        // 处理非心跳消息
        if (!Utils.isHeartBeat(baseInfo)) {
            // 说明消息被拆包了，剩余可读的字节数比消息体大小少
            if (byteBuf.readableBytes() < size) {
                byteBuf.resetReaderIndex();
                return;
            }
            // 读取消息体的数据
            byte[] payload = new byte[size];
            byteBuf.readBytes(payload);
            // 根据消息头里的位来获取序列化和压缩方式
            Serialization serialization = SerializationFactory.get(baseInfo);
            Compressor compressor = CompressorFactory.get(baseInfo);
            if (Utils.isRequest(baseInfo)) {
                body = serialization.deserialize(compressor.unCompress(payload), Request.class);
            } else {
                body = serialization.deserialize(compressor.unCompress(payload), Response.class);
            }
            // 根据消息头和消息体来构建完整的消息，继续pipeline传递
            Header header = new Header(magic, version, baseInfo, messageId, size);
            Message message = new Message(header, body);
            out.add(message);
        }
    }
}
