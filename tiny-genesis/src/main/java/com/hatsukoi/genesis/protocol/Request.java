package com.hatsukoi.genesis.protocol;

import java.io.Serializable;

/**
 * 请求实体类
 * @author gaoweilin
 * @date 2022/06/21 Tue 3:56 AM
 */
public class Request implements Serializable {
    /**
     * 请求的服务名
     */
    private String serviceName;
    /**
     * 请求的方法名
     */
    private String methodName;
    /**
     * 请求方法的参数类型
     */
    private Class[] argTypes;
    /**
     * 请求方法的参数
     */
    private Object[] args;

    public Request(String serviceName, String methodName, Object[] args) {
        this.serviceName = serviceName;
        this.methodName = methodName;
        this.args = args;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class[] getArgTypes() {
        return argTypes;
    }

    public void setArgTypes(Class[] argTypes) {
        this.argTypes = argTypes;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
