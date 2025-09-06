package com.github.cadecode.uniboot.starter.web.advisor;

import com.github.cadecode.uniboot.common.enums.ErrorCode;
import com.github.cadecode.uniboot.common.exception.GeneralException;
import com.github.cadecode.uniboot.starter.web.enums.WebErrorEnum;
import com.github.cadecode.uniboot.starter.web.model.ApiResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * GeneralException 统一处理器
 *
 * @author Cade Li
 * @date 2022/5/8
 */
@Slf4j
@RestControllerAdvice
public class GeneralExceptionAdvisor {

    /**
     * 处理 GeneralException
     */
    @ExceptionHandler(GeneralException.class)
    public ApiResult<Object> handleGeneralException(GeneralException e, HttpServletRequest request) {
        // 特殊处理接口返回 null 的情况
        if (Objects.equals(WebErrorEnum.RES_BODY_NULL.getCode(), e.getErrorCode().getCode())) {
            return ApiResult.ok(null);
        }
        log.error("Handle general exception, uri:{} =>", request.getRequestURI(), e);
        return ApiResult.error(e.getErrorCode()).moreInfo(e.getMoreInfo());
    }

    /**
     * 兜底处理一般异常
     */
    @ExceptionHandler(Exception.class)
    public ApiResult<Object> handleException(Exception e, HttpServletRequest request) {
        log.error("Handle exception, uri:{} =>", request.getRequestURI(), e);
        return ApiResult.error(ErrorCode.UNKNOWN).moreInfo(e.getMessage());
    }
}
