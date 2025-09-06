package com.github.cadecode.uniboot.common.enums;

import com.github.cadecode.uniboot.common.consts.ApiStatus;

/**
 * 通用异常错误码接口
 * <p>使用枚举类继承该类，便于统一管理异常信息
 *
 * @author Cade Li
 * @date 2022/5/8
 */
public interface ErrorCode {

    String DEFAULT_CODE = "UNKNOWN";
    String DEFAULT_MESSAGE = "未知错误";
    int DEFAULT_STATUS = ApiStatus.SERVER_ERROR;

    default String getCode() {
        return DEFAULT_CODE;
    }

    default String getMessage() {
        return DEFAULT_MESSAGE;
    }

    default int getStatus() {
        return DEFAULT_STATUS;
    }

    /**
     * 未知异常
     */
    ErrorCode UNKNOWN = new ErrorCode() {};
}
