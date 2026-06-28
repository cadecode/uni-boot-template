# Logging Guidelines

> Log levels, format conventions, and what to log.

---

## Logging Framework

**SLF4J + Logback** (Spring Boot default). Lombok `@Slf4j` for logger injection.

```java
@Slf4j
@Service
public class UserService {
    public User getById(Long id) {
        log.info("Query user by id: {}", id);
        // ...
    }
}
```

---

## Log Levels

| Level | Usage | Example |
|-------|-------|---------|
| **ERROR** | System errors requiring immediate attention | `log.error("Handle exception, uri:{} =>", uri, e)` |
| **WARN** | Expected but noteworthy conditions | `log.warn("Retry exhausted, giving up")` |
| **INFO** | Key business events, state transitions | `log.info("订单创建成功, orderId: {}, userId: {}", oid, uid)` |
| **DEBUG** | Detailed execution flow, SQL, parameters | `log.debug("SQL: {}, params: {}", sql, params)` |

**Rule**: Production default level = INFO. DEBUG for development only.

---

## Message Format

Use SLF4J `{}` placeholders — NOT string concatenation:

```java
// ✅ Correct — lazy evaluation, no cost when level is disabled
log.info("User login, username: {}, ip: {}", username, ip);

// ❌ Wrong — string concatenation always evaluated
log.info("User login, username: " + username + ", ip: " + ip);
```

---

## Exception Logging

Always include the exception object as the LAST argument to capture full stack trace:

```java
// ✅ Correct — stack trace included
log.error("Handle general exception, uri:{} =>", request.getRequestURI(), e);

// ❌ Wrong — stack trace lost
log.error("Handle general exception, uri:{} =>" + e.getMessage());
```

---

## Real Patterns from Codebase

### Exception Handlers

```java
// GeneralExceptionAdvisor
log.error("Handle general exception, uri:{} =>", request.getRequestURI(), e);

// WebExceptionAdvisor — parameter validation failures
log.error("Handle validation exception, uri:{} =>", request.getRequestURI(), e);
```

### Service Layer

```java
log.info("订单创建成功，orderId: {}, userId: {}", orderId, userId);
log.debug("SQL: {}, params: {}", sql, params);
```

### DataSource Switching (AOP)

```java
log.info("Switch datasource to {}, execute method [{}]", dsKey, methodName);
log.info("Reset datasource to {}", dsKey);
```

---

## Sensitive Data

Never log passwords, tokens, or PII in clear text:

```java
// ❌ Wrong — passwords in logs
log.info("User login, username: {}, password: {}", username, password);

// ✅ Correct — mask sensitive data
log.info("User login, username: {}, password: {}", username, "******");
```

---

## Starter/Config Logging

Auto-configuration classes log at DEBUG/INFO for diagnostics:

```java
@Slf4j
@Configuration
public class SwaggerAutoConfig {
    @Bean
    @ConditionalOnProperty(name = "x-boot.swagger.title")
    public OpenAPI openAPI(SwaggerProperties prop) {
        log.info("Swagger enabled, title: {}", prop.getTitle());
        // ...
    }
}
```

---

## Forbidden Patterns

- ❌ Do NOT use `System.out.println()` — always SLF4J
- ❌ Do NOT use string concatenation in log messages — use `{}` placeholders
- ❌ Do NOT log passwords, tokens, API keys
- ❌ Do NOT pass `e.getMessage()` instead of `e` — lose stack trace
- ❌ Do NOT log at ERROR level for expected business failures — use WARN
