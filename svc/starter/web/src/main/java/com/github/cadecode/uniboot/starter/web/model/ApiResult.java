package com.github.cadecode.uniboot.starter.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.github.cadecode.uniboot.common.consts.ApiStatus;
import com.github.cadecode.uniboot.common.enums.ErrorCode;
import lombok.Data;

import java.util.Objects;

/**
 * API 统一 JSON 返回格式
 *
 * @author Cade Li
 * @date 2022/5/8
 */
@Data
public class ApiResult<T> {

    /**
     * 与 http 状态码保持一致
     */
    private Integer status;

    /**
     * 数据
     */
    @JsonInclude(Include.NON_NULL)
    private T data;

    /**
     * 异常信息
     */
    @JsonInclude(Include.NON_NULL)
    private ErrorMessage error;

    /**
     * API 异常信息类
     */
    @Data
    public static class ErrorMessage {

        /**
         * 错误码
         */
        private String code;

        /**
         * 错误信息
         */
        private String message;

        /**
         * 路径
         */
        private String path;

        /**
         * 更多错误信息
         */
        @JsonInclude(Include.NON_NULL)
        private String moreInfo;
    }

    /**
     * 返回没有错误的响应
     *
     * @param data 数据
     * @return ApiResult
     */
    public static <T> ApiResult<T> ok(T data) {
        return ApiResult.of(null, data).status(ApiStatus.OK);
    }

    /**
     * 返回错误的响应，使用 ErrorCode 的状态码
     *
     * @return ApiResult
     */
    public static ApiResult<Object> error(ErrorCode code) {
        return ApiResult.of(code, null);
    }

    /**
     * 根据 ErrorCode 返回响应
     *
     * @param code 错误信息
     * @param data 数据
     * @return ApiResult
     */
    public static <T> ApiResult<T> of(ErrorCode code, T data) {
        ApiResult<T> result = new ApiResult<>();
        result.setData(data);
        if (Objects.nonNull(code)) {
            // http status
            result.setStatus(code.getStatus());
            // message
            ErrorMessage error = new ErrorMessage();
            error.setCode(code.getCode());
            error.setMessage(code.getMessage());
            result.setError(error);
        }
        return result;
    }

    /**
     * 修改状态码
     *
     * @param status http 状态码
     * @return ApiResult
     */
    public ApiResult<T> status(int status) {
        setStatus(status);
        return this;
    }

    /**
     * 修改路径
     *
     * @param path 路径
     * @return ApiResult
     */
    public ApiResult<T> path(String path) {
        if (Objects.nonNull(error)) {
            error.setPath(path);
        }
        return this;
    }

    /**
     * 修改更多错误信息
     *
     * @param moreInfo 更多错误信息
     * @param params   字符串模板参数
     * @return ApiResult
     */
    public ApiResult<T> moreInfo(String moreInfo, Object... params) {
        if (Objects.nonNull(error)) {
            error.setMoreInfo(moreInfo);
        }
        return this;
    }
}
