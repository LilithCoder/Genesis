package com.hatsukoi.genesis.demo.Service;

import com.hatsukoi.genesis.demo.ProviderService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * @author gaoweilin
 * @date 2022/06/08 Wed 11:22 PM
 */
@Service(value = "ConsumerService")
public class ConsumerServiceImpl {
    private static final Logger logger = Logger.getLogger(ConsumerServiceImpl.class);

    // refer
    private ProviderService providerService;

    public void invoke() {
        String resp = providerService.hello("mock request param");
        logger.info("[Consumer] Response from Provider: " + resp);
    }
}
