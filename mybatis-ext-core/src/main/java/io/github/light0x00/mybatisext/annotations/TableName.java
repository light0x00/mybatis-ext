package io.github.light0x00.mybatisext.annotations;

import java.lang.annotation.*;

/**
 * @author light
 * @since 2022/2/19
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface TableName {

    String value() default "";

    String schema() default "";
}
