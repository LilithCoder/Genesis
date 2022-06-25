package com.hatsukoi.genesis.annotation;

import java.lang.annotation.*;

/**
 * 服务类扫描（包路径）
 * @author gaoweilin
 * @date 2022/06/26 Sun 3:03 AM
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcScan {
    String basePackage();
}
