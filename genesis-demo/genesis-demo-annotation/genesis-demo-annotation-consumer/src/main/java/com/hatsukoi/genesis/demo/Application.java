package com.hatsukoi.genesis.demo;

import com.hatsukoi.genesis.demo.Service.ConsumerServiceImpl;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * @author gaoweilin
 * @date 2022/06/08 Wed 2:58 AM
 */
public class Application {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConsumerConfiguration.class);
        context.start();
        ConsumerServiceImpl service = context.getBean("ConsumerService", ConsumerServiceImpl.class);
        service.invoke();

    }

    @Configuration
    static class ConsumerConfiguration {

    }
}
