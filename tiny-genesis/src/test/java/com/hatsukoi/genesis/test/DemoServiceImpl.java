package com.hatsukoi.genesis.test;

import org.apache.log4j.Logger;

/**
 * @author gaoweilin
 * @date 2022/06/24 Fri 2:54 AM
 */
public class DemoServiceImpl implements DemoService {
    private static final Logger logger = Logger.getLogger(DemoServiceImpl.class);

    /**
     * 服务提供者的接口具体实现
     * @param param
     * @return
     */
    @Override
    public String hello(String param) {
        // TODO: 补上消费者的信息
        String requestMsg = "[Provider] Request from Consumer: " + "Parameter: " + param;
        logger.info(requestMsg);
        try {
            logger.info("Running Provider Service...");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            logger.error("Provider Service interrupted Exception occurrs", e);
            e.printStackTrace();
        }
        String resp = "mock response data";
        return resp;
    }
}
