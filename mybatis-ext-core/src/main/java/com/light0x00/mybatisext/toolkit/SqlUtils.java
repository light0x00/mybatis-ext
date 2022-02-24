package com.light0x00.mybatisext.toolkit;

/**
 * @author light
 * @since 2022/2/22
 */
public class SqlUtils {

    public static String transformObjAsText(Object obj) {
        if (obj instanceof CharSequence || obj instanceof Character) {
            return "'" + obj + "'";
        } else {
            return String.valueOf(obj);
        }
    }

}
