package com.hatsukoi.genesis.demo;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author gaoweilin
 * @date 2022/06/08 Wed 2:57 AM
 */
public class Application {
    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ProviderConfiguration.class);
        context.start();
        System.in.read();
    }

    @Configuration
    static class ProviderConfiguration {

    }

}
