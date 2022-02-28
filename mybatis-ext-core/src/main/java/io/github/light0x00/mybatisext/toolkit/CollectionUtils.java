package io.github.light0x00.mybatisext.toolkit;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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

    public static <T> List<T> toList(T... arr) {
        return Arrays.stream(arr)
                .collect(Collectors.toList());
    }
}
