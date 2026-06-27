package com.github.cadecode.uniboot.starter.web.advisor;

import com.github.cadecode.uniboot.common.enums.ErrorCode;
import com.github.cadecode.uniboot.common.exception.RateLimitException;
import com.github.cadecode.uniboot.starter.web.enums.WebErrorEnum;
import com.github.cadecode.uniboot.starter.web.model.ApiResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * 框架异常处理器
 *
 * @author Cade Li
 * @date 2022/5/30
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebExceptionAdvisor {

    /**
     * 限流异常
     */
    @ExceptionHandler(value = RateLimitException.class)
    public ApiResult<Object> handleValidationException(RateLimitException e, HttpServletRequest request) {
        log.error("Handle rateLimit exception, uri:{} =>", request.getRequestURI(), e);
        return ApiResult.error(WebErrorEnum.REQUEST_RATE_LIMITED).moreInfo("请稍后再尝试访问").path(request.getRequestURI());
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(value = BindException.class)
    public ApiResult<Object> handleBindException(BindException e, HttpServletRequest request) {
        log.error("Handle validation exception, uri:{} =>", request.getRequestURI(), e);
        // 获取错误信息，并拼接
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(o -> "[" + o.getField() + "]" + o.getDefaultMessage())
                .collect(Collectors.joining(","));
        return ApiResult.error(WebErrorEnum.VALIDATED_ERROR).moreInfo(msg).path(request.getRequestURI());
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(value = ValidationException.class)
    public ApiResult<Object> handleValidationException(ValidationException e, HttpServletRequest request) {
        log.error("Handle validation exception, uri:{} =>", request.getRequestURI(), e);
        String msg = e.getMessage();
        return ApiResult.error(WebErrorEnum.VALIDATED_ERROR).moreInfo(msg).path(request.getRequestURI());
    }

    /**
     * Spring MVC 异常类型和错误枚举的映射
     */
    private static final Map<Class<?>, ErrorCode> MVC_EXP_CODE_MAP = new HashMap<>() {{
        // 请求参数错误
        put(ServletRequestBindingException.class, WebErrorEnum.REQ_PARAM_INVALID);
        // 请求体错误
        put(HttpMessageNotReadableException.class, WebErrorEnum.REQ_BODY_INVALID);
        // content-type 错误
        put(HttpMediaTypeNotSupportedException.class, WebErrorEnum.MEDIA_TYPE_NO_SUPPORT);
        // 参数类型转换错误
        put(TypeMismatchException.class, WebErrorEnum.PARAM_TYPE_CONVERT_ERROR);
        // 请求方法不支持
        put(HttpRequestMethodNotSupportedException.class, WebErrorEnum.METHOD_NO_SUPPORT);
        // 静态资源不存在
        put(NoResourceFoundException.class, WebErrorEnum.NO_RESOURCE_FOUND);
    }};

    /**
     * 处理 Spring MVC 参数异常
     */
    @ExceptionHandler({
            ServletRequestBindingException.class,
            HttpMessageNotReadableException.class,
            HttpMediaTypeNotSupportedException.class,
            TypeMismatchException.class,
            HttpRequestMethodNotSupportedException.class,
            NoResourceFoundException.class
    })
    public ApiResult<Object> handleMvcException(Exception e, HttpServletRequest request) {
        log.error("Handle spring mvc exception, uri:{} =>", request.getRequestURI(), e);
        String requestURI = request.getRequestURI();
        // 根据异常类型查找 map 中的 code 枚举
        ErrorCode errorCode = MVC_EXP_CODE_MAP.entrySet()
                .stream()
                .filter(o -> o.getKey().isAssignableFrom(e.getClass()))
                .map(Entry::getValue)
                .findFirst()
                .orElse(ErrorCode.UNKNOWN);
        return ApiResult.error(errorCode).moreInfo(e.getMessage()).path(requestURI);
    }
}
