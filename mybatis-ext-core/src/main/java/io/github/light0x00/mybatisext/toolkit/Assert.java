package io.github.light0x00.mybatisext.toolkit;

import io.github.light0x00.mybatisext.exceptions.MyBatisExtException;

/**
 * @author light
 * @since 2022/2/20
 */
public class Assert {

    public static void notNull(Object obj, String message, String args) {
        if (obj == null) {
            throw new MyBatisExtException(message, args);
        }
    }
}
