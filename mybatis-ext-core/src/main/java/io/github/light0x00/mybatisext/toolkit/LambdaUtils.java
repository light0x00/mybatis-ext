package io.github.light0x00.mybatisext.toolkit;

import io.github.light0x00.mybatisext.exceptions.MyBatisExtException;
import lombok.Data;

import java.io.*;
import java.lang.invoke.SerializedLambda;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author light
 * @since 2022/7/10
 */
public class LambdaUtils {

    /**
     * SerializedLambda 反序列化缓存
     */
    private static final Map<String, WeakReference<SerializedLambda>> FUNC_CACHE = new ConcurrentHashMap<>();


    @Data
    static class Fuck implements Serializable{
        private String username;
    }

    public static void main(String[] args) {

        System.out.println(resolve(Fuck::getUsername));
    }

    public static <T> SerializedLambda resolve(SFunction<T, ?> func) {
        Class<?> clazz = func.getClass();
        String name = clazz.getName();
        return Optional.ofNullable(FUNC_CACHE.get(name))
                .map(WeakReference::get)
                .orElseGet(() -> {
                    SerializedLambda lambda = resolve2(func);
                    FUNC_CACHE.put(name, new WeakReference<>(lambda));
                    return lambda;
                });
    }

    public static SerializedLambda resolve2(SFunction<?, ?> lambda) {
        if (!lambda.getClass().isSynthetic()) {
            throw new MyBatisExtException("该方法仅能传入 lambda 表达式产生的合成类");
        }
        try (ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(serialize(lambda))) {
            @Override
            protected Class<?> resolveClass(ObjectStreamClass objectStreamClass) throws IOException, ClassNotFoundException {
                Class<?> clazz;
                try {
                    clazz = toClassConfident(objectStreamClass.getName());
                } catch (Exception ex) {
                    clazz = super.resolveClass(objectStreamClass);
                }
                return clazz == java.lang.invoke.SerializedLambda.class ? SerializedLambda.class : clazz;
            }
        }) {
            return (SerializedLambda) objIn.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new MyBatisExtException("This is impossible to happen", e);
        }
    }

    /**
     * <p>
     * 请仅在确定类存在的情况下调用该方法
     * </p>
     *
     * @param name 类名称
     * @return 返回转换后的 Class
     */
    public static Class<?> toClassConfident(String name) {
        try {
            return Class.forName(name, false, getDefaultClassLoader());
        } catch (ClassNotFoundException e) {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException ex) {
                throw new MyBatisExtException("找不到指定的class！请仅在明确确定会有 class 的时候，调用该方法", e);
            }
        }
    }

    /**
     * Serialize the given object to a byte array.
     *
     * @param object the object to serialize
     * @return an array of bytes representing the object in a portable fashion
     */
    public static byte[] serialize(Object object) {
        if (object == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(object);
            oos.flush();
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to serialize object of type: " + object.getClass(), ex);
        }
        return baos.toByteArray();
    }


    /**
     * Return the default ClassLoader to use: typically the thread context
     * ClassLoader, if available; the ClassLoader that loaded the ClassUtils
     * class will be used as fallback.
     * <p>Call this method if you intend to use the thread context ClassLoader
     * in a scenario where you clearly prefer a non-null ClassLoader reference:
     * for example, for class path resource loading (but not necessarily for
     * {@code Class.forName}, which accepts a {@code null} ClassLoader
     * reference as well).
     *
     * @return the default ClassLoader (only {@code null} if even the system
     * ClassLoader isn't accessible)
     * @see Thread#getContextClassLoader()
     * @see ClassLoader#getSystemClassLoader()
     * @since 3.3.2
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = LambdaUtils.class.getClassLoader();
            if (cl == null) {
                // getClassLoader() returning null indicates the bootstrap ClassLoader
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                }
            }
        }
        return cl;
    }
}
