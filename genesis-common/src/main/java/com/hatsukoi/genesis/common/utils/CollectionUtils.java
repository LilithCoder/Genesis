package com.hatsukoi.genesis.common.utils;

import java.util.Collection;

/**
 * @author gaoweilin
 * @date 2022/06/05 Sun 3:19 PM
 */
public class CollectionUtils {

    /**
     * Return true if provided collection is null or empty
     * @param collection
     * @return
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
}
