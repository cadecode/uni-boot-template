package com.github.cadecode.uniboot.starter.web.enums;

import com.github.cadecode.uniboot.common.consts.ApiStatus;
import com.github.cadecode.uniboot.common.enums.ErrorCode;
import lombok.Getter;

/**
 * 框架异常枚举
 *
 * @author Cade Li
 * @date 2022/5/30
 */
@Getter
public enum WebErrorEnum implements ErrorCode {

    VALIDATED_ERROR("WEB_1000", "参数校验不通过") {
        @Override
        public int getStatus() {
            return ApiStatus.BAD_REQUEST;
        }
    },
    REQ_PARAM_INVALID("WEB_1001", "请求参数无效") {
        @Override
        public int getStatus() {
            return ApiStatus.BAD_REQUEST;
        }
    },
    REQ_BODY_INVALID("WEB_1002", "请求体无效") {
        @Override
        public int getStatus() {
            return ApiStatus.BAD_REQUEST;
        }
    },
    MEDIA_TYPE_NO_SUPPORT("WEB_1003", "MediaType 不被支持") {
        @Override
        public int getStatus() {
            return ApiStatus.BAD_REQUEST;
        }
    },
    PARAM_TYPE_CONVERT_ERROR("WEB_1004", "参数类型转换错误") {
        @Override
        public int getStatus() {
            return ApiStatus.BAD_REQUEST;
        }
    },
    REQUEST_RATE_LIMITED("WEB_1005", "请求已被限流") {
        @Override
        public int getStatus() {
            return ApiStatus.TOO_MANY_REQUESTS;
        }
    },
    METHOD_NO_SUPPORT("WEB_1006", "请求方法不支持") {
        @Override
        public int getStatus() {
            return ApiStatus.BAD_REQUEST;
        }
    },
    NO_RESOURCE_FOUND("WEB_1007", "请求资源不存在") {
        @Override
        public int getStatus() {
            return ApiStatus.NOT_FOUND;
        }
    },
    /**
     * 特殊处理接口返回 null 的情况
     */
    RES_BODY_NULL("WEB_9999", "响应体为空");

    private final String code;

    private final String message;

    WebErrorEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
