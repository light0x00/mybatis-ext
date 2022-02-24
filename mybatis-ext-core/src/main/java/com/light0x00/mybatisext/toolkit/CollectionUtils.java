package com.light0x00.mybatisext.toolkit;

import java.util.Collection;

/**
 * @author light
 * @since 2022/2/23
 */
public class CollectionUtils {

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.size() == 0;
    }
}
