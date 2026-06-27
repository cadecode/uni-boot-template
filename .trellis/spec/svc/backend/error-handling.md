# Error Handling Guidelines

> Exception hierarchy, unified response format, and global exception handling.

---

## Exception Hierarchy

```
RuntimeException
 └── BaseException                  (base, supports StrUtil.format templates)
      └── GeneralException          (core business exception, carries ErrorCode)
           ├── ExtensionException   (extension point failures)
           ├── RateLimitException   (rate limiting)
           └── HelperException      (utility/tool failures)
```

### BaseException

Supports Hutool `StrUtil.format` message templates:

```java
// svc/common/.../exception/BaseException.java
public class BaseException extends RuntimeException {
    public BaseException(String message, Object... params) {
        super(StrUtil.format(message, params));
    }
}
```

### GeneralException

The primary business exception — always carries an `ErrorCode`:

```java
// svc/common/.../exception/GeneralException.java
@Getter
public class GeneralException extends BaseException {
    private final ErrorCode errorCode;
    private final String moreInfo;
}
```

### ErrorCode Interface

Enums implement `ErrorCode` for centralized error management:

```java
// svc/common/.../enums/ErrorCode.java
public interface ErrorCode {
    String DEFAULT_CODE = "UNKNOWN";
    String DEFAULT_MESSAGE = "未知错误";
    int DEFAULT_STATUS = ApiStatus.SERVER_ERROR;  // 500

    default String getCode() { return DEFAULT_CODE; }
    default String getMessage() { return DEFAULT_MESSAGE; }
    default int getStatus() { return DEFAULT_STATUS; }

    ErrorCode UNKNOWN = new ErrorCode() {};  // Default unknown error
}
```

### WebErrorEnum (Concrete Implementation)

Real project pattern — per-constant HTTP status override via anonymous class:

```java
// svc/starter/web/.../enums/WebErrorEnum.java
@Getter
public enum WebErrorEnum implements ErrorCode {
    VALIDATED_ERROR("WEB_1000", "参数校验不通过") {
        @Override public int getStatus() { return ApiStatus.BAD_REQUEST; }
    },
    REQ_PARAM_INVALID("WEB_1001", "请求参数无效") {
        @Override public int getStatus() { return ApiStatus.BAD_REQUEST; }
    },
    REQUEST_RATE_LIMITED("WEB_1005", "请求已被限流") {
        @Override public int getStatus() { return ApiStatus.TOO_MANY_REQUESTS; }
    },
    NO_RESOURCE_FOUND("WEB_1007", "请求资源不存在") {
        @Override public int getStatus() { return ApiStatus.NOT_FOUND; }
    },
    RES_BODY_NULL("WEB_9999", "响应体为空");  // Special: returns 200

    private final String code;
    private final String message;
}
```

### ApiStatus (HTTP Status Constants)

```java
// svc/common/.../consts/ApiStatus.java
public interface ApiStatus {
    int OK = 200;
    int BAD_REQUEST = 400;
    int NOT_FOUND = 404;
    int TOO_MANY_REQUESTS = 429;
    int SERVER_ERROR = 500;
    // ... other standard codes
}
```

---

## Throwing Exceptions

### Basic Usage

```java
// With ErrorCode enum
throw GeneralException.of(WebErrorEnum.USER_NOT_FOUND, "userId: {}", userId);

// With custom ErrorCode
throw GeneralException.of(ErrorCode.of("CUSTOM_001", "自定义错误"), "详情: {}", detail);

// Unknown error
throw GeneralException.of("订单处理失败, orderId: {}", orderId);

// Wrapping a cause
throw GeneralException.of(e, "外部服务调用失败, service: {}", serviceName);
```

### Specialized Exceptions

```java
throw ExtensionException.of("Pipeline执行失败", context);
throw RateLimitException.of("请求频率超限, ip: {}", ip);
throw HelperException.of("JSON序列化失败", e);
```

---

## Unified Response Format

All API responses use `ApiResult<T>`:

```java
@Data
public class ApiResult<T> {
    private Integer status;           // HTTP status code
    @JsonInclude(Include.NON_NULL)
    private T data;                  // Business data
    @JsonInclude(Include.NON_NULL)
    private ErrorMessage error;      // Error details

    @Data
    public static class ErrorMessage {
        private String code;          // Error code
        private String message;        // Error message
        private String path;           // Request path
        @JsonInclude(Include.NON_NULL)
        private String moreInfo;       // Additional info
    }
}
```

### Factory Methods

```java
// Success response (status=200)
ApiResult.ok(data);

// Error response (status from ErrorCode)
ApiResult.error(errorCode).moreInfo("额外信息").path("/api/user/123");

// Custom status
ApiResult.of(errorCode, data).status(201);
```

### Response Examples

Success:
```json
{ "status": 200, "data": {"id": 1, "name": "张三"}, "error": null }
```

Error:
```json
{
  "status": 404,
  "data": null,
  "error": {
    "code": "USER_1001",
    "message": "用户不存在",
    "path": "/api/user/123",
    "moreInfo": "userId: 123"
  }
}
```

---

## Global Exception Handlers

### 1. WebExceptionAdvisor (HIGHEST_PRECEDENCE)

Handles Spring MVC framework exceptions first:

```java
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebExceptionAdvisor {
    // Exception-to-ErrorCode mapping
    private static final Map<Class<?>, ErrorCode> MVC_EXP_CODE_MAP = new HashMap<>() {{
        put(ServletRequestBindingException.class, WebErrorEnum.REQ_PARAM_INVALID);
        put(HttpMessageNotReadableException.class, WebErrorEnum.REQ_BODY_INVALID);
        put(HttpMediaTypeNotSupportedException.class, WebErrorEnum.MEDIA_TYPE_NO_SUPPORT);
        put(TypeMismatchException.class, WebErrorEnum.PARAM_TYPE_CONVERT_ERROR);
        put(HttpRequestMethodNotSupportedException.class, WebErrorEnum.METHOD_NO_SUPPORT);
        put(NoResourceFoundException.class, WebErrorEnum.NO_RESOURCE_FOUND);
    }};

    @ExceptionHandler({BindException.class})
    public ApiResult<Object> handleBindException(BindException e, HttpServletRequest request) {
        String msg = e.getBindingResult().getFieldErrors().stream()
            .map(o -> "[" + o.getField() + "]" + o.getDefaultMessage())
            .collect(Collectors.joining(","));
        return ApiResult.error(WebErrorEnum.VALIDATED_ERROR).moreInfo(msg);
    }
}
```

### 2. GeneralExceptionAdvisor (fallback)

Handles `GeneralException` and the final `Exception` fallback:

```java
@Slf4j
@RestControllerAdvice
public class GeneralExceptionAdvisor {
    @ExceptionHandler(GeneralException.class)
    public ApiResult<Object> handleGeneralException(GeneralException e, HttpServletRequest request) {
        log.error("Handle general exception, uri:{} =>", request.getRequestURI(), e);
        return ApiResult.error(e.getErrorCode()).moreInfo(e.getMoreInfo());
    }

    @ExceptionHandler(Exception.class)
    public ApiResult<Object> handleException(Exception e, HttpServletRequest request) {
        log.error("Handle exception, uri:{} =>", request.getRequestURI(), e);
        return ApiResult.error(ErrorCode.UNKNOWN).moreInfo(e.getMessage());
    }
}
```

**Order**: WebExceptionAdvisor (HIGHEST) → GeneralExceptionAdvisor (default)

### 3. ApiResultAdvisor (ResponseBodyAdvice)

Wraps controller return values into `ApiResult<T>` automatically:

```java
// svc/starter/web/.../advisor/ApiResultAdvisor.java
@RestControllerAdvice
public class ApiResultAdvisor implements ResponseBodyAdvice<Object> {
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, ...) {
        // If already ApiResult → just set path
        if (body instanceof ApiResult<?> result) {
            response.setStatusCode(HttpStatus.valueOf(result.getStatus()));
            return result.path(path);
        }
        // Check @ApiFormat annotation to decide wrapping
        // String body → wrap as JSON: JacksonUtil.toJson(ApiResult.ok(body))
        // null body with @ApiFormat → throw RES_BODY_NULL
        // Otherwise → ApiResult.ok(body)
    }
}
```

**Key behaviors**:
- `String` return type → serialized as JSON string (not `text/plain`)
- `null` return with `@ApiFormat` → throws `GeneralException.of(WebErrorEnum.RES_BODY_NULL)`
- Controller returns raw `ApiResult<T>` → sets HTTP status from `ErrorCode.getStatus()`

---

## @ApiFormat Annotation

Controls whether response is wrapped with `ApiResult`:

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiFormat {
    boolean value() default true;
}
```

Usage:
```java
@ApiFormat  // Class-level: all methods wrapped
@RestController
public class UserController { }

// File download — no wrapping needed
@GetMapping("/download")
public void download(HttpServletResponse response) { }
```

---

## Forbidden Patterns

- ❌ Do NOT throw raw `RuntimeException` — use `GeneralException.of(...)`
- ❌ Do NOT return raw error strings in controllers — rely on exception handlers
- ❌ Do NOT catch `Exception` in service layer and return null — let it propagate
- ❌ Do NOT include stack traces in API responses — log them, return error code
