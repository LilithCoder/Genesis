package com.hatsukoi.genesis.common.utils;

import com.hatsukoi.genesis.common.URL;
import static com.hatsukoi.genesis.common.constant.CommonConstant.*;

/**
 * @author gaoweilin
 * @date 2022/06/05 Sun 6:05 PM
 */
public class UrlUtils {
    public static boolean isMatch(URL consumer, URL provider) {
        // 匹配 Consumer 和 Provider 的接口（优先取 interface 参数，其次再取 path）
        // 双方接口相同或者其中一方为“*”，则匹配成功，执行下一步
        String consumerInterface = consumer.getServiceInterface();
        String providerInterface = provider.getServiceInterface();
        if (!ANY_VALUE.equals(consumerInterface) &&
                !ANY_VALUE.equals(providerInterface) &&
                !consumerInterface.equals(providerInterface)) {
            return false;
        }
        // TODO: 匹配 Consumer 和 Provider 的 category
        // TODO: 检测 Consumer URL 和 Provider URL 中的 enable 参数是否符合条件
        // TODO: 检测 Consumer 和 Provider 端的 group、version 以及 classifier 是否符合条件
        return true;
    }
}
