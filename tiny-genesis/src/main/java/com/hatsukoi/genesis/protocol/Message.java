package com.hatsukoi.genesis.protocol;

/**
 * 消息实体类
 * @author gaoweilin
 * @date 2022/06/21 Tue 4:04 AM
 */
public class Message<T> {
    /**
     * 消息头
     */
    private Header header;
    /**
     * 消息体
     */
    private T content;

    public Message(Header header, T content) {
        this.header = header;
        this.content = content;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
