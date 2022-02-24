package com.light0x00.mybatisext.annotations;

import java.lang.annotation.*;

/**
 * @author light
 * @since 2022/2/20
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface Column {
    boolean primary() default false;
}
