package com.hatsukoi.genesis.protocol;

import java.io.Serializable;

/**
 * 响应实体类
 * @author gaoweilin
 * @date 2022/06/21 Tue 3:56 AM
 */
public class Response implements Serializable {
    /**
     * 响应状态码（默认0为响应正常）
     */
    private int code = 0;
    /**
     * 响应错误信息
     */
    private String errMsg;
    /**
     * 响应结果
     */
    private Object data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
