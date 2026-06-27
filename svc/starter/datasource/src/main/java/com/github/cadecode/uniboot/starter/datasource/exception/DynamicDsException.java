package com.github.cadecode.uniboot.starter.datasource.exception;

import com.github.cadecode.uniboot.common.exception.BaseException;

/**
 * 动态数据源异常
 *
 * @author Cade Li
 * @date 2023/6/9
 */
public class DynamicDsException extends BaseException {
    public DynamicDsException() {
    }

    public DynamicDsException(String message, Object... params) {
        super(message, params);
    }

    public DynamicDsException(String message, Throwable cause, Object... params) {
        super(message, cause, params);
    }

    public DynamicDsException(Throwable cause) {
        super(cause);
    }

    public DynamicDsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object... params) {
        super(message, cause, enableSuppression, writableStackTrace, params);
    }
}
