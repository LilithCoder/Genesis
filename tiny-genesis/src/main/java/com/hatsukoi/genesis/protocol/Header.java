package com.hatsukoi.genesis.protocol;

/**
 * 协议消息头（固定16字节）
 * @author gaoweilin
 * @date 2022/06/21 Tue 3:46 AM
 */
public class Header {
    /**
     * 魔数（协议识别）
     */
    private short magic;
    /**
     * 协议版本
     */
    private byte version;
    /**
     * 基本信息
     * 0:   消息类型（请求、响应）
     * 1~2: 序列化方式
     * 3~4: 压缩方式
     * 5~6: 请求类型（正常请求、心跳请求）
     */
    private byte baseInfo;
    /**
     * 消息ID
     */
    private Long messageId;
    /**
     * 消息体长度
     */
    private Integer size;

    public Header(short magic, byte version) {
        this.magic = magic;
        this.version = version;
    }

    public Header(short magic, byte version, byte baseInfo, Long messageId, Integer size) {
        this.magic = magic;
        this.version = version;
        this.baseInfo = baseInfo;
        this.messageId = messageId;
        this.size = size;
    }

    public short getMagic() {
        return magic;
    }

    public void setMagic(short magic) {
        this.magic = magic;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public byte getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(byte baseInfo) {
        this.baseInfo = baseInfo;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
