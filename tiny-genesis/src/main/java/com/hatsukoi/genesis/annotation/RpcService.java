package com.hatsukoi.genesis.annotation;

/**
 * @author gaoweilin
 * @date 2022/06/26 Sun 4:04 AM
 */
public @interface RpcService {
    public String name() default "";
}
