package io.github.bookiosk.exception;


import io.github.bookiosk.util.ExceptionUtil;
import io.github.bookiosk.util.StrUtil;

/**
 * @author bookiosk
 */
public class AsyncException extends RuntimeException{
    private static final long serialVersionUID = 8247610319171014183L;

    public AsyncException(Throwable e) {
        super(ExceptionUtil.getMessage(e), e);
    }

    public AsyncException(String message) {
        super(message);
    }

    public AsyncException(String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params));
    }

    public AsyncException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public AsyncException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }

    public AsyncException(Throwable throwable, String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params), throwable);
    }
}
