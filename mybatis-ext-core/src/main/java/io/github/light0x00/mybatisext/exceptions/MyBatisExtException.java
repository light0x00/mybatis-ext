package io.github.light0x00.mybatisext.exceptions;

import java.text.MessageFormat;

/**
 * @author light
 * @since 2022/2/20
 */
public class MyBatisExtException extends RuntimeException {

    public MyBatisExtException(String message, Object... args) {
        super(MessageFormat.format(message, args));
    }

    public MyBatisExtException(Throwable cause, String message, Object... args) {
        super(MessageFormat.format(message, args), cause);
    }
}
