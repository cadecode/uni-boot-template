package com.github.cadecode.uniboot.common.exception;

/**
 * 限流异常类
 *
 * @author Cade Li
 * @date 2023/6/9
 */
public class RateLimitException extends BaseException {
    public RateLimitException() {
    }

    public RateLimitException(String message, Object... params) {
        super(message, params);
    }

    public RateLimitException(String message, Throwable cause, Object... params) {
        super(message, cause, params);
    }

    public RateLimitException(Throwable cause) {
        super(cause);
    }

    public RateLimitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object... params) {
        super(message, cause, enableSuppression, writableStackTrace, params);
    }
}
