package com.hatsukoi.genesis.demo;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * @author gaoweilin
 * @date 2022/06/08 Wed 2:50 AM
 */
@Service
public class ProviderServiceImpl implements ProviderService {
    private static final Logger logger = Logger.getLogger(ProviderServiceImpl.class);

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
