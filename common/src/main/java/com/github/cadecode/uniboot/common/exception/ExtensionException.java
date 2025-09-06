package com.github.cadecode.uniboot.common.exception;

/**
 * 扩展异常
 *
 * @author Cade Li
 * @since 2023/6/24
 */
public class ExtensionException extends BaseException {

    public ExtensionException() {
    }

    public ExtensionException(String message, Object... params) {
        super(message, params);
    }

    public ExtensionException(String message, Throwable cause, Object... params) {
        super(message, cause, params);
    }

    public ExtensionException(Throwable cause) {
        super(cause);
    }

    public ExtensionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object... params) {
        super(message, cause, enableSuppression, writableStackTrace, params);
    }
}
