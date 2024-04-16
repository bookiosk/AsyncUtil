package io.github.bookiosk.util;

import cn.hutool.core.util.StrUtil;
import io.github.bookiosk.exception.AsyncException;

import java.util.function.Supplier;

/**
 * @author bookiosk
 */
public class AssertUtil {


    public static <X extends Throwable> void isTrue(boolean expression, Supplier<? extends X> supplier) throws X {
        if (!expression) {
            throw supplier.get();
        }
    }

    public static void isTrue(boolean expression, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        isTrue(expression, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
    }

    public static void isTrue(boolean expression) throws IllegalArgumentException {
        isTrue(expression, "[Assertion failed] - this expression must be true");
    }

    public static <T, X extends Throwable> T notNull(T object, Supplier<X> errorSupplier) throws X {
        if (null == object) {
            throw errorSupplier.get();
        }
        return object;
    }

    public static <T> T notNull(T object, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        return notNull(object, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
    }
    public static <T> T notNull(T object) throws IllegalArgumentException {
        return notNull(object, "[Assertion failed] - this argument is required; it must not be null");
    }

    // ----------------------------------------------------------------------------------------------------------- Check empty
    public static <T extends CharSequence, X extends Throwable> T notEmpty(T text, Supplier<X> errorSupplier) throws X {
        if (StrUtil.isEmpty(text)) {
            throw errorSupplier.get();
        }
        return text;
    }

    public static <T extends CharSequence> T notEmpty(T text, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        return notEmpty(text, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
    }

    public static <T extends CharSequence> T notEmpty(T text) throws IllegalArgumentException {
        return notEmpty(text, "[Assertion failed] - this String argument must have length; it must not be null or empty");
    }
}
