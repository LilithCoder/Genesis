package com.hatsukoi.genesis.common.extension;

import java.lang.annotation.*;

/**
 * A Marker indicates that the interface being decorated is an extension interface
 * When loading the implementation of the marked interface, if the extension is not
 * specified, the value annotated by SPI will be used as default extension name,
 * the related extension implementation class will be loaded
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SPI {
    /**
     * Specify the default extension name
     * @return
     */
    String value() default "";
}
