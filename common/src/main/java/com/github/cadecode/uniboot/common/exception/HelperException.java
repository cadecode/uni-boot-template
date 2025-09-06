package com.github.cadecode.uniboot.common.exception;

/**
 * 辅助类异常
 *
 * @author Cade Li
 * @date 2023/6/9
 */
public class HelperException extends BaseException {
    public HelperException() {
    }

    public HelperException(String message, Object... params) {
        super(message, params);
    }

    public HelperException(String message, Throwable cause, Object... params) {
        super(message, cause, params);
    }

    public HelperException(Throwable cause) {
        super(cause);
    }

    public HelperException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object... params) {
        super(message, cause, enableSuppression, writableStackTrace, params);
    }
}
