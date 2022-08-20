package io.github.light0x00.mybatisext.toolkit;

import java.lang.invoke.SerializedLambda;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * @author light
 * @since 2022/2/19
 */
public class ReflectionUtils {

    public static Class<?>[] resolveParameterizedClasses(Class<?> clazz) {
        List<Class<?>> parameterizedClasses = new LinkedList<>();
        for (Type genericInterface : clazz.getGenericInterfaces()) {
            if (genericInterface instanceof ParameterizedType) {
                Type[] actualTypeArguments = ((ParameterizedType) genericInterface).getActualTypeArguments();
                for (Type actualTypeArgument : actualTypeArguments) {
                    parameterizedClasses.add((Class<?>) actualTypeArgument);
                }
            }
        }
        return parameterizedClasses.toArray(new Class[0]);
    }

    public static List<Field> getInstanceSerializableFieldList(Class<?> clazz) {
        return getFieldList(clazz, (f) -> !Modifier.isStatic(f.getModifiers()) && !Modifier.isTransient(f.getModifiers()));
    }

    public static List<Field> getInstanceFieldList(Class<?> clazz) {
        return getFieldList(clazz, (f) -> !Modifier.isStatic(f.getModifiers()));
    }

    public static List<Field> getClassFieldList(Class<?> clazz) {
        return getFieldList(clazz, (f) -> Modifier.isStatic(f.getModifiers()));
    }

    private static List<Field> getFieldList(Class<?> clazz, Predicate<? super Field> filter) {
        if (Objects.isNull(clazz)) {
            return Collections.emptyList();
        }
        //得到子类字段
        Field[] fields = clazz.getDeclaredFields();
        //得到所有直接或间接父类字段
        List<Field> superFields = new LinkedList<>();
        Class<?> currentClass = clazz.getSuperclass();
        while (currentClass != null) {
            Field[] declaredFields = currentClass.getDeclaredFields();
            Collections.addAll(superFields, declaredFields);
            currentClass = currentClass.getSuperclass();
        }
        // 父子字段去重后合并
        Map<String, Field> fieldMap = excludeOverrideSuperField(fields, superFields);

        return fieldMap.values().stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    private static Map<String, Field> excludeOverrideSuperField(Field[] fields, List<Field> superFieldList) {
        Map<String, Field> fieldMap = Stream.of(fields).collect(toMap(Field::getName, identity(),
                (u, v) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", u));
                },
                LinkedHashMap::new));
        superFieldList.stream().filter(field -> !fieldMap.containsKey(field.getName()))
                .forEach(f -> fieldMap.put(f.getName(), f));
        return fieldMap;
    }

}
