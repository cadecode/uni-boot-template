# Backend Quality Guidelines

> Code standards, validation, API design conventions, Git commit format, and forbidden patterns.

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 17 | Language (LTS) |
| Spring Boot | 3.2.5 | Application framework |
| MyBatis-Flex | 1.8.8 | ORM |
| Druid | 1.2.21 | Connection pool |
| Knife4j (SpringDoc) | 4.4.0 | API documentation |
| Hutool | 5.8.12 | General utility library |
| MapStruct | 1.5.2 | Object mapping |
| Lombok | 1.18.32 | Boilerplate reduction |
| PageHelper | 6.1.0 | Pagination |
| Jasypt | 3.0.5 | Config encryption |

---

## Class Naming Conventions

### Layer Suffixes

| Layer | Pattern | Example |
|-------|---------|---------|
| Controller | `XxxController` | `UserController` |
| Service interface | `XxxService` | `UserService` |
| Service impl | `XxxServiceImpl` | `UserServiceImpl` |
| Mapper (DAO) | `XxxMapper` | `UserMapper` |
| Manager | `XxxManager` | `OrderManager` |
| Feign client | `XxxClient` | `UserClient` |
| Aspect | `XxxAspect` | `LogAspect` |
| Listener | `XxxListener` | `BaseEntityListener` |
| Interceptor | `XxxInterceptor` | `AuthInterceptor` |
| Config | `XxxConfig` | `WebMvcConfig` |
| Converter | `XxxConvert` | `UserConvert` |

### Utility Class Suffixes

| Type | Suffix | Example |
|------|--------|---------|
| Static utility | `XxxUtil` | `JacksonUtil`, `SpringUtil` |
| Spring-managed utility | `XxxKit` | `GenCodeKit`, `DbDocKit` |

### Enum Suffix

Always `XxxEnum`:
```java
public enum UserTypeEnum implements ErrorCode { ... }
public enum OrderStatusEnum { ... }
```

---

## Method Naming

### CRUD Operations

| Operation | Recommended Names |
|-----------|-------------------|
| Query single | `getById`, `getOne` |
| Query list | `list`, `selectList`, `queryList` |
| Paginate | `page`, `selectPage` |
| Count | `count` |
| Exists check | `exists`, `checkExists` |
| Create | `save`, `insert`, `add` |
| Batch create | `saveBatch`, `insertBatch` |
| Update | `update`, `modify` |
| Batch update | `updateBatch` |
| Delete | `removeById`, `deleteById` |
| Batch delete | `removeByIds`, `deleteBatch` |

### Business Operations

| Intent | Recommended Names |
|--------|-------------------|
| Enable/Disable | `enableXxx`, `disableXxx` |
| Approve/Reject | `approveXxx`, `rejectXxx` |
| Validate | `validateXxx`, `checkXxx` |
| Process | `processXxx`, `handleXxx` |
| Calculate | `calculateXxx` |
| Convert | `convertXxx`, `toXxx`, `fromXxx` |

---

## Validation

### Bean Validation (JSR-380)

```java
@Data
public class OrderDTO {
    @NotNull(message = "订单ID不能为空")
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度2-20")
    private String username;

    @Min(value = 1, message = "金额必须大于0")
    private BigDecimal amount;
}

// Controller activates validation
@PostMapping("/order")
public ApiResult<Long> saveOrder(@RequestBody @Valid OrderDTO dto) {
    return ApiResult.ok(orderService.save(dto));
}
```

### Programmatic Validation

Use `AssertUtil` from `common`:

```java
AssertUtil.isNotNull(order, "订单不能为空");
AssertUtil.isTrue(order.getAmount() > 0, "订单金额必须大于0");
```

---

## REST API Design

### URL Convention

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    @GetMapping("/{id}")
    public ApiResult<User> getById(@PathVariable Long id) { }

    @PostMapping
    public ApiResult<Long> save(@RequestBody @Valid UserDTO dto) { }

    @PutMapping("/{id}")
    public ApiResult<Boolean> update(@PathVariable Long id, @RequestBody UserDTO dto) { }

    @DeleteMapping("/{id}")
    public ApiResult<Boolean> delete(@PathVariable Long id) { }

    @GetMapping
    public ApiResult<PageResult<User>> page(PageParams params) { }
}
```

### Swagger Annotations

```java
@Tag(name = "用户管理", description = "用户相关接口")
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Operation(summary = "根据ID查询用户")
    @Parameter(name = "id", description = "用户ID", required = true)
    @GetMapping("/{id}")
    public ApiResult<User> getById(@PathVariable Long id) { }
}
```

---

## Jackson Configuration

```java
@Configuration
public class JacksonConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> {
            builder.failOnEmptyBeans(false);
            // Long → String (prevents JS precision loss)
            builder.serializerByType(Long.class, ToStringSerializer.instance);
            builder.serializerByType(Long.TYPE, ToStringSerializer.instance);
            // LocalDateTime format
            builder.serializers(new LocalDateTimeSerializer(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        };
    }
}
```

**Key points**:
- `Long` serialized as `String` in JSON to avoid JavaScript number precision loss
- Date format: `yyyy-MM-dd HH:mm:ss`
- Empty beans don't throw serialization errors

---

## Git Commit Convention

Follow **Angular Conventional Commits**:

```
<type>(<scope>): <subject>
```

### Types

| Type | Purpose |
|------|---------|
| `feat` | New feature |
| `fix` | Bug fix |
| `docs` | Documentation only |
| `style` | Code formatting (no logic change) |
| `refactor` | Code restructure (no feature/fix) |
| `perf` | Performance improvement |
| `test` | Adding/updating tests |
| `build` | Build system, external dependencies |
| `ci` | CI configuration changes |
| `chore` | Other changes (non-src, non-test) |
| `revert` | Revert previous commit |

### Scope

Module or layer name in parentheses: `(server)`, `(common)`, `(codegen)`, `(mybatis)`, etc. Optional for project-wide changes.

### Examples from this project

```
refactor: 代码目录调整至 svc 目录下
docs: add README + AGENTS + 命名规则 markdown doc
test(server): add application tests
refactor(common): 移除不必要的泛型
chore(server): remove redundant config
feat(codegen): 添加代码生成工具类
```

### Rules

- Subject line: ≤ 50 chars, starts with verb, imperative mood
- No line exceeds 72 chars
- Chinese or English subject is acceptable (be consistent within a commit)
- Body/footer separated by blank lines for complex changes

---

## Forbidden Patterns Summary

- ❌ Pinyin in identifiers — use English
- ❌ Abbreviations except well-known (`id`, `url`, `api`, `dto`, `vo`)
- ❌ Primitive `boolean` for DB entity fields — use `Boolean` + `Flag` suffix
- ❌ `isXxx` boolean naming for DB entities
- ❌ `System.out.println` — use SLF4J
- ❌ Throwing raw `RuntimeException` — use `GeneralException.of(...)`
- ❌ Returning raw error strings from controllers
- ❌ String concatenation in log messages
- ❌ Logging passwords/tokens/secrets
- ❌ Plural package names (`controllers` → `controller`)
- ❌ Upward dependency between modules
