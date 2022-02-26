package io.github.light0x00.mybatisext.toolkit;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author light
 * @since 2022/2/19
 */
public class StringUtils {
    private static final String EMPTY = "";
    private static final char UNDERLINE = '_';

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    public static boolean isBlank(CharSequence cs) {
        if (cs != null) {
            int length = cs.length();
            for (int i = 0; i < length; i++) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    public static String camelToUnderline(String param) {
        if (isBlank(param)) {
            return EMPTY;
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append(UNDERLINE);
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    public static String trimHeadDot(String s) {
        if (isEmpty(s)) {
            return s;
        }
        if (s.indexOf(0) == '.') {
            return s.substring(1);
        }
        return s;
    }

    public static String trimTailDot(String s) {
        if (isEmpty(s)) {
            return s;
        }
        if (s.lastIndexOf(0) == '.') {
            return s.substring(0, s.length() - 1);
        }
        return s;
    }

    public static String combineWithExactlyOneDot(String... parts) {
        return Arrays.stream(parts)
                .filter(StringUtils::isNotEmpty)
                .map(StringUtils::trimHeadDot)
                .map(StringUtils::trimTailDot)
                .collect(Collectors.joining("."));

    }
}
