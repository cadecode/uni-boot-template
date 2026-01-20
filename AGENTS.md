# AGENTS.md

uni-boot-template 技术架构与模块详细说明

## 目录

- [架构设计](#架构设计)
- [模块详解](#模块详解)
- [扩展点机制](#扩展点机制)
- [设计模式](#设计模式)
- [最佳实践](#最佳实践)

---

## 架构设计

### 整体架构图

```
┌─────────────────────────────────────────────────────────┐
│                    Server Layer                         │
│  ┌──────────────────────────────────────────────────┐  │
│  │  server/admin (Business Application)            │  │
│  │  - Controllers                                   │  │
│  │  - Services                                      │  │
│  │  - Business Logic                                │  │
│  └──────────────────────────────────────────────────┘  │
└────────────────────┬────────────────────────────────────┘
                     │ depends on
┌────────────────────▼────────────────────────────────────┐
│                  Framework Layer                       │
│  ┌──────────────────────────────────────────────────┐  │
│  │  framework (Integration Layer)                  │  │
│  │  - JacksonConfig                                 │  │
│  │  - ThreadPoolConfig                              │  │
│  │  - WebMvcConfig                                  │  │
│  └──────────────────────────────────────────────────┘  │
└────────────────────┬────────────────────────────────────┘
                     │ depends on
┌────────────────────▼────────────────────────────────────┐
│                  Starter Layer                          │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐  │
│  │  starter │ │  starter │ │  starter │ │  starter │  │
│  │   web    │ │ swagger  │ │ mybatis  │ │datasource│  │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘  │
└────────────────────┬────────────────────────────────────┘
                     │ depends on
┌────────────────────▼────────────────────────────────────┐
│                  Common Layer                           │
│  ┌──────────────────────────────────────────────────┐  │
│  │  common (Foundation Layer)                      │  │
│  │  - Utils (JacksonUtil, SpringUtil)            │  │
│  │  - Exceptions (BaseException, GeneralException) │  │
│  │  - Extensions (Pipeline, Plugin)               │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

### 分层原则

1. **单向依赖**: 上层依赖下层，下层不依赖上层
2. **职责清晰**: 每层有明确的职责边界
3. **可独立演进**: 下层可独立测试和升级
4. **可替换性**: 通过接口和抽象支持替换

### 依赖流向

```
dependency (BOM)
    ↓
common (Foundation)
    ↓
starter (Auto-configuration)
    ↓
framework (Integration)
    ↓
server (Application)
```

---

## 模块详解

### 1. dependency 模块

**职责**: 集中管理所有第三方依赖版本

**核心功能**:
- 版本号统一管理
- 避免版本冲突
- 简化依赖升级

**关键配置**:
```xml
<properties>
    <spring-boot.version>3.2.5</spring-boot.version>
    <mybatis-flex.version>1.8.8</mybatis-flex.version>
    <knife4j.version>4.4.0</knife4j.version>
</properties>
```

---

### 2. common 模块

**职责**: 提供公共基础设施

#### 2.1 包结构

```
common/
├── consts/              # 常量定义
├── enums/               # 枚举
│   ├── ErrorCode.java   # 错误码接口
│   └── ExtensionType.java # 扩展类型
├── exception/           # 异常处理
│   ├── BaseException.java
│   ├── GeneralException.java
│   ├── ExtensionException.java
│   ├── RateLimitException.java
│   └── HelperException.java
├── extension/           # 扩展点机制
│   ├── pipeline/        # Pipeline扩展点
│   └── plugin/          # Plugin扩展点
└── util/                # 工具类
    ├── AssertUtil.java  # 断言工具
    ├── JacksonUtil.java # JSON工具
    ├── JacksonXUtil.java # XML工具
    ├── SpringUtil.java  # Spring工具
    └── TreeUtil.java    # 树结构工具
```

#### 2.2 异常体系

```java
BaseException (RuntimeException)
    └── GeneralException
        ├── ExtensionException
        ├── RateLimitException
        └── HelperException
```

**设计特点**:
- 支持Hutool模板格式化: `throw GeneralException.of("用户{}不存在", userId)`
- 携带ErrorCode对象
- 支持额外上下文信息

#### 2.3 工具类

| 工具类 | 功能 | 使用频率 |
|--------|------|----------|
| AssertUtil | 运行时断言，抛出GeneralException | 低（待使用） |
| JacksonUtil | JSON序列化/反序列化 | 高 |
| JacksonXUtil | XML序列化/反序列化 | 低（待使用） |
| SpringUtil | Spring容器操作 | 低（待使用） |
| TreeUtil | 平铺列表转树形结构 | 低（待使用） |

---

### 3. starter 模块

**职责**: 提供可插拔的自动配置功能

#### 3.1 starter-web

**功能**: Web层基础设施

**核心组件**:

##### 3.1.1 ApiResult - 统一响应格式

```java
@Data
public class ApiResult<T> {
    private Integer status;        // HTTP状态码
    private T data;               // 业务数据
    private ErrorMessage error;   // 错误信息

    @Data
    public static class ErrorMessage {
        private String code;      // 错误码
        private String message;   // 错误消息
        private String path;      // 请求路径
        private String moreInfo;  // 额外信息
    }

    // 静态工厂方法
    public static <T> ApiResult<T> ok(T data) {
        return ApiResult.of(null, data).status(ApiStatus.OK);
    }

    public static ApiResult<Object> error(ErrorCode code) {
        return ApiResult.of(code, null);
    }
}
```

**响应示例**:
```json
{
  "status": 200,
  "data": {"id": 1, "name": "张三"},
  "error": null
}
```

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

##### 3.1.2 @ApiFormat - 控制响应包装

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiFormat {
    boolean value() default true;
}
```

**使用方式**:
```java
@ApiFormat  // 类级别：所有方法都包装
@RestController
public class UserController {
    @GetMapping("/user/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getById(id);
    }
}

@RestController
public class FileController {
    @GetMapping("/download")
    public void download(HttpServletResponse response) {
        // 不包装，直接输出文件流
    }
}
```

##### 3.1.3 异常处理器

**GeneralExceptionAdvisor**:
```java
@RestControllerAdvice
public class GeneralExceptionAdvisor {
    @ExceptionHandler(GeneralException.class)
    public ApiResult<Object> handleGeneralException(
        GeneralException e,
        HttpServletRequest request
    ) {
        log.error("Handle general exception, uri:{} =>", request.getRequestURI(), e);
        return ApiResult.error(e.getErrorCode())
                    .moreInfo(e.getMoreInfo())
                    .path(request.getRequestURI());
    }
}
```

**WebExceptionAdvisor** (最高优先级):
```java
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebExceptionAdvisor {
    // MVC框架异常映射
    private static final Map<Class<?>, ErrorCode> MVC_EXP_CODE_MAP = Map.of(
        ServletRequestBindingException.class, WebErrorEnum.REQ_PARAM_INVALID,
        HttpMessageNotReadableException.class, WebErrorEnum.REQ_BODY_INVALID,
        HttpMediaTypeNotSupportedException.class, WebErrorEnum.MEDIA_TYPE_NO_SUPPORT,
        TypeMismatchException.class, WebErrorEnum.PARAM_TYPE_CONVERT_ERROR,
        HttpRequestMethodNotSupportedException.class, WebErrorEnum.METHOD_NO_SUPPORT,
        NoResourceFoundException.class, WebErrorEnum.NO_RESOURCE_FOUND
    );

    @ExceptionHandler(BindException.class)
    public ApiResult<Object> handleBindException(BindException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
            .map(o -> "[" + o.getField() + "]" + o.getDefaultMessage())
            .collect(Collectors.joining(","));
        return ApiResult.error(WebErrorEnum.VALIDATED_ERROR).moreInfo(msg);
    }
}
```

#### 3.2 starter-swagger

**功能**: API文档自动生成

**配置**:
```java
@EnableKnife4j
@Configuration
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnProperty(name = "uni-boot.swagger.title")
public class SwaggerAutoConfig {
    @Bean
    public OpenAPI openAPI(SwaggerProperties prop) {
        Contact contact = new Contact()
            .name(prop.getContactName())
            .url(prop.getContactUrl())
            .email(prop.getContactEmail());
        return new OpenAPI()
            .info(new Info()
                .title(prop.getTitle())
                .description(prop.getDescription())
                .version(prop.getVersion())
                .contact(contact));
    }
}
```

#### 3.3 starter-mybatis

**功能**: MyBatis-Flex集成

**核心组件**:

##### 3.3.1 BaseEntity - 实体基类

```java
@Data
public interface BaseEntity {
    Date getCreateTime();
    void setCreateTime(Date createTime);
    String getCreateUser();
    void setCreateUser(String createUser);
    Date getUpdateTime();
    void setUpdateTime(Date updateTime);
    String getUpdateUser();
    void setUpdateUser(String updateUser);
}
```

##### 3.3.2 BaseEntityListener - 自动填充

```java
@Component
public class BaseEntityListener implements EntityListener<BaseEntity> {
    @Override
    public void onInsert(BaseEntity entity) {
        Date now = new Date();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        // 从上下文获取当前用户
        entity.setCreateUser(SpringUtil.getProperty("user.id"));
    }

    @Override
    public void onUpdate(BaseEntity entity) {
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(SpringUtil.getProperty("user.id"));
    }
}
```

##### 3.3.3 自定义类型处理器

**BoolToIntTypeHandler**:
```java
public class BoolToIntTypeHandler extends BaseTypeHandler<Boolean> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter ? 1 : 0);
    }

    @Override
    public Boolean getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return rs.getInt(columnName) == 1;
    }
}
```

**ObjToStrTypeHandler** (JSON字段):
```java
public class ObjToStrTypeHandler extends BaseTypeHandler<Object> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JacksonUtil.toJson(parameter));
    }

    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return JacksonUtil.toBean(json, Object.class);
    }
}
```

#### 3.4 MyBatis-Flex 多数据源

**功能**: MyBatis-Flex 原生多数据源支持（自 1.0.6 版本起内置）

MyBatis-Flex 内置了完善的多数据源支持，不需要借助第三方插件或依赖，开箱即用。

##### 3.4.1 配置多数据源

```yaml
mybatis-flex:
  datasource:
    # 主数据源（默认）
    master:
      type: druid
      url: jdbc:mysql://localhost:3306/master-db
      username: root
      password: root
    # 从数据源1
    slave1:
      type: com.zaxxer.hikari.HikariDataSource
      url: jdbc:mysql://localhost:3306/slave1
      username: root
      password: root
    # 从数据源2
    slave2:
      type: com.zaxxer.hikari.HikariDataSource
      url: jdbc:mysql://localhost:3306/slave2
      username: root
      password: root
```

##### 3.4.2 使用 @UseDataSource 注解

在 Mapper、Service 或 Controller 类或方法上添加 `@UseDataSource` 注解指定数据源：

```java
// 在 Mapper 类上指定数据源
@UseDataSource("slave1")
public interface UserMapper {
    List<User> queryUsers();
}

// 在 Mapper 方法上指定数据源
public interface UserMapper {
    @UseDataSource("slave1")
    List<User> queryUsers();

    @UseDataSource("master")
    void saveUser(User user);
}

// 在 Service 类上指定数据源
@Service
@UseDataSource("slave1")
public class UserService {
    public List<User> listUsers() {
        return userMapper.selectAll();
    }
}

// 在 Service 方法上指定数据源
@Service
public class UserService {
    @UseDataSource("master")
    public void saveUser(User user) {
        userMapper.insert(user);
    }

    @UseDataSource("slave1")
    public List<User> listUsers() {
        return userMapper.selectAll();
    }
}
```

##### 3.4.3 编码方式切换数据源

使用 `DataSourceKey.use()` 方法在代码中动态切换数据源：

```java
// 方式1：try-finally 手动清理
try {
    DataSourceKey.use("slave2");
    List<User> users = userMapper.selectAll();
} finally {
    DataSourceKey.clear();
}

// 方式2：使用 Lambda 自动清理
List<User> users = DataSourceKey.use("slave2", () -> {
    return userMapper.selectAll();
});
```

##### 3.4.4 读写分离

MyBatis-Flex 的读写分离功能基于多数据源实现，通过实现 `DataSourceShardingStrategy` 接口自定义分片策略：

```java
public class MyShardingStrategy implements DataSourceShardingStrategy {
    @Override
    public String doSharding(String currentDataSourceKey,
                            Object mapper,
                            Method mapperMethod,
                            Object[] methodArgs) {
        // 读写分离逻辑
        // 方法名以 insert/delete/update 开头，使用 master
        if (StringUtil.startWithAny(
            mapperMethod.getName(), "insert", "delete", "update")) {
            return "master";
        }
        // 其他方法，随机使用 slave1 或 slave2
        return "slave" + (RandomUtil.randomInt(1, 3));
    }
}
```

启动时注册策略：

```java
@Configuration
public class FlexConfiguration {
    @PostConstruct
    public void registerShardingStrategy() {
        DataSourceManager.setDataSourceShardingStrategy(new MyShardingStrategy());
    }
}
```

**特性**:
- 支持多种数据源：Druid、HikariCP、DBCP2、BeeCP 等
- 支持多层注解：Mapper、Service、Controller
- 支持动态表达式：`@UseDataSource(value = "#myExpression")`
- 支持读写分离和负载均衡
- 自动切换与恢复

#### 3.5 starter-actuator

**功能**: Spring Boot Actuator监控

---

### 4. framework 模块

**职责**: 框架层统一配置，集成所有starter

#### 4.1 JacksonConfig

```java
@Configuration
public class JacksonConfig implements Jackson2ObjectMapperBuilderCustomizer {
    @Override
    public void customize(Jackson2ObjectMapperBuilder builder) {
        builder
            // 日期格式化
            .dateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
            // Long转String，防止前端溢出
            .serializerByType(Long.class, ToStringSerializer.instance)
            // 空对象不失败
            .failOnEmptyBeans(false);
    }
}
```

#### 4.2 ThreadPoolConfig

```java
@Configuration
public class ThreadPoolConfig {

    // 定时任务线程池
    @Bean("taskScheduler")
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
        scheduler.setThreadNamePrefix("task-scheduler-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        return scheduler;
    }

    // 异步任务线程池
    @Bean("asyncExecutor")
    public ThreadPoolTaskExecutor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(16);
        executor.setMaxPoolSize(32);
        executor.setQueueCapacity(10000);
        executor.setThreadNamePrefix("async-executor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setAllowCoreThreadTimeOut(true);
        return executor;
    }
}
```

#### 4.3 WebMvcConfig

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加拦截器
    }
}
```

---

### 5. codegen 模块

**职责**: 代码生成

#### 5.1 GenCodeKit - 代码生成器

```java
@Component
public class GenCodeKit {
    public void codegen(String outputDir, String basePackage, String author,
                        String tablePrefix, String[] tableNames) {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.enableEntity()
            .setWithLombok(true)
            .setWithSwagger(true)
            .setImplInterfaces(BaseEntity.class, Serializable.class);

        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setTablePrefix(tablePrefix);
        strategyConfig.setGenerateTable(tableNames);

        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setBasePackage(basePackage);

        Generator generator = new Generator(dataSource, globalConfig);
        generator.generate();
    }
}
```

#### 5.2 EntityConvertGenerator - 自定义生成器

```java
public class EntityConvertGenerator implements IGenerator {
    @Override
    public void generate(Table table, GlobalConfig globalConfig) {
        // 生成MapStruct转换器
    }
}
```

#### 5.3 DbDocKit - 数据库文档生成

```java
@Component
public class DbDocKit {
    public void dbdoc(String fileType, String fileName, String fileOutputDir,
                      String version, String description, String tablePrefix,
                      String tableSuffix, String[] tableNames) {
        EngineConfig engineConfig = EngineConfig.builder()
            .fileOutputDir(fileOutputDir)
            .openOutputDir(false)
            .fileType(DocumentType.valueOf(fileType))
            .build();

        Configuration config = Configuration.builder()
            .version(version)
            .description(description)
            .dataSource(dataSource)
            .engineConfig(engineConfig)
            .produceConfig(getProcessConfig(tableNames, tablePrefix, tableSuffix))
            .build();

        new DocumentationExecute(config).execute();
    }
}
```

---

### 6. server 模块

**职责**: 业务应用

#### 6.1 admin 模块

```
admin/
├── AdminApplication.java  # 启动类
└── pipline/                # Pipeline示例
    ├── TestContext.java
    ├── TestFilter1.java
    ├── TestFilter2.java
    └── TestFilter3.java
```

---

## 扩展点机制

### Pipeline 扩展点（开发中）

**核心概念**: 基于责任链模式，支持配置驱动的业务流程编排

**说明**: Pipeline 扩展点机制正在开发中，暂不支持注解声明方式。当前版本提供了基础接口和实现框架。

#### 核心接口

```java
// Pipeline上下文
public interface PipelineContext {
    ExtensionType getPipelineType();    // 业务场景
    FilterSelector getFilterSelector(); // 过滤器选择器
    boolean continueChain();           // 是否继续执行
}

// Pipeline过滤器
public interface PipelineFilter<T extends PipelineContext> {
    void doFilter(T context, PipelineFilterChain<T> filterChain);
}

// Pipeline链
public interface PipelineFilterChain<T> {
    void filter(T context);
    void next();
}
```

#### 使用场景

Pipeline 适用于需要按顺序执行多个处理步骤的业务场景：

- **订单处理流程**: 订单校验 → 库存检查 → 价格计算 → 优惠计算
- **用户注册流程**: 参数校验 → 风控检查 → 初始化数据 → 发送通知
- **审批流程**: 提交 → 部门审批 → 财务审批 → 审批完成

#### 配置示例

```yaml
uni-boot:
  extension:
    pipeline:
      filter-selectors:
        ORDER_CREATE:
          - orderValidateFilter
          - inventoryCheckFilter
          - priceCalculateFilter
        ORDER_PAY:
          - orderValidateFilter
          - paymentCheckFilter
```

#### 关键特性

- **条件执行**: FilterSelector 控制是否执行特定 Filter
- **中断链**: context.continueChain() = false 可中断执行
- **异步执行**: 可扩展为异步执行
- **配置驱动**: 通过 YAML 配置灵活组装执行链

---

### Plugin 扩展点

**核心概念**: 基于策略模式，支持运行时动态选择实现

#### 核心接口

```java
// Plugin上下文
public interface PluginContext {
    ExtensionType getExtensionType();
}

// Plugin服务
public interface PluginService<T extends PluginContext> {
    boolean supports(T context);  // 是否支持此场景
    void execute(T context);       // 执行逻辑
}
```

#### 实现示例

**1. 定义Plugin**

```java
@Component
public class AliyunSmsPlugin implements PluginService<SmsContext> {
    @Override
    public boolean supports(SmsContext context) {
        return context.getExtensionType() == ExtensionType.SMS
            && "aliyun".equals(context.getProvider());
    }

    @Override
    public void execute(SmsContext context) {
        // 阿里云短信发送逻辑
        log.info("使用阿里云发送短信: {}", context.getPhone());
    }
}

@Component
public class TencentSmsPlugin implements PluginService<SmsContext> {
    @Override
    public boolean supports(SmsContext context) {
        return context.getExtensionType() == ExtensionType.SMS
            && "tencent".equals(context.getProvider());
    }

    @Override
    public void execute(SmsContext context) {
        // 腾讯云短信发送逻辑
        log.info("使用腾讯云发送短信: {}", context.getPhone());
    }
}
```

**2. 执行Plugin**

```java
@Service
public class SmsService {
    @Autowired
    private PluginExecutor pluginExecutor;

    public void sendSms(String phone, String provider) {
        SmsContext context = new SmsContext(ExtensionType.SMS);
        context.setPhone(phone);
        context.setProvider(provider);

        pluginExecutor.execute(context);
    }
}
```

**特点**:
- 运行时动态选择
- 基于条件匹配
- 支持多个实现竞争

---

## 设计模式

### 1. 责任链模式 (Chain of Responsibility)
 
**应用场景**: Pipeline扩展点（开发中）
 
**实现**:
```java
public class DefaultPipelineFilterChain<T> implements PipelineFilterChain<T> {
    private PipelineFilter<T> current;
    private PipelineFilterChain<T> next;
 
    @Override
    public void filter(T context) {
        current.doFilter(context, this);
    }
 
    @Override
    public void next() {
        if (next != null) {
            next.filter(context);
        }
    }
}
```
 
**优势**:
- 解耦发送者和接收者
- 动态组合执行链
- 易于扩展

### 2. 策略模式 (Strategy)

**应用场景**: Plugin扩展点

**实现**:
```java
public interface PluginService<T> {
    boolean supports(T context);
    void execute(T context);
}
```

**优势**:
- 运行时动态选择
- 避免if-else
- 易于扩展新策略

### 3. 模板方法模式 (Template Method)

**应用场景**: AbstractPipelineFilter

**实现**:
```java
public abstract class AbstractPipelineFilter<T extends PipelineContext> implements PipelineFilter<T> {
    @Override
    public void doFilter(T context, PipelineFilterChain<T> filterChain) {
        // 1. 检查是否应该执行
        if (!shouldExecute(context)) {
            return;
        }

        // 2. 执行业务逻辑（子类实现）
        handle(context);

        // 3. 继续执行链
        if (context.continueChain()) {
            filterChain.next();
        }
    }

    protected abstract void handle(T context);
}
```

**优势**:
- 定义算法骨架
- 子类实现细节
- 代码复用

### 4. 工厂模式 (Factory)

**应用场景**: 代码生成器

**实现**:
```java
GeneratorFactory.registerGenerator("entityConvert", new EntityConvertGenerator());
```

**优势**:
- 解耦创建和使用
- 统一创建逻辑
- 易于扩展

### 5. 代理模式 (Proxy/AOP)
 
**应用场景**:
- 统一响应包装
- 异常处理
- 日志记录
 
**实现**:
```java
@Aspect
@Component
public class ApiResultAdvisor implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }
 
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        // 统一响应包装
        return ApiResult.ok(body);
    }
}
```

**优势**:
- 解耦业务逻辑和横切关注点
- 代码复用
- 集中管理

### 6. 建造者模式 (Builder)

**应用场景**: ApiResult、PipelineGenerator

**实现**:
```java
public class ApiResult<T> {
    public static <T> ApiResult<T> of(ErrorCode code, T data) {
        ApiResult<T> result = new ApiResult<>();
        result.setCode(code.getCode());
        result.setMessage(code.getMessage());
        result.setData(data);
        return result;
    }

    public ApiResult<T> status(int status) {
        this.status = status;
        return this;  // 链式调用
    }

    public ApiResult<T> path(String path) {
        this.error.setPath(path);
        return this;
    }
}
```

**优势**:
- 流畅API
- 可读性强
- 分步骤构建复杂对象

### 7. 观察者模式 (Observer)

**应用场景**: Spring事件机制

**实现**:
```java
// 发布事件
SpringUtil.publishEvent(new OrderCreatedEvent(order));

// 监听事件
@Component
public class OrderEventListener {
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        // 发送通知
        // 更新缓存
    }
}
```

**优势**:
- 解耦发布者和订阅者
- 一对多通知
- 异步处理

---

## 最佳实践

### 1. 命名规范

#### 类命名
- Entity: `User`, `Order`, `OrderItem`
- DTO: `UserDTO`, `OrderDTO`
- VO: `UserVO`, `OrderVO`
- Controller: `UserController`, `OrderController`
- Service: `UserService`, `OrderService`
- Mapper: `UserMapper`, `OrderMapper`
- Filter: `OrderValidateFilter`, `InventoryCheckFilter`
- Plugin: `AliyunSmsPlugin`, `TencentSmsPlugin`

#### 方法命名
- 查询: `getById`, `selectList`, `page`, `query`
- 新增: `save`, `insert`, `add`
- 更新: `update`, `modify`
- 删除: `remove`, `delete`
- 统计: `count`, `sum`

#### 包命名
```
com.github.cadecode.uniboot.{module}.{submodule}
例: com.github.cadecode.uniboot.admin.service
```

### 2. 异常处理规范

#### 抛出异常
```java
// 通用异常
throw GeneralException.of(WebErrorEnum.USER_NOT_FOUND, "userId: {}", userId);

// 自定义错误码
throw GeneralException.of(ErrorCode.of("CUSTOM_001", "自定义错误"), "详情: {}", detail);

// 扩展点异常
throw ExtensionException.of("扩展点执行失败", context);
```

#### 捕获异常
```java
try {
    // 业务逻辑
} catch (GeneralException e) {
    // 已知异常，记录日志后重新抛出或处理
    log.warn("业务异常: {}", e.getMessage());
    throw e;
} catch (Exception e) {
    // 未知异常，记录完整堆栈
    log.error("系统异常", e);
    throw GeneralException.of(ErrorCode.UNKNOWN, "系统繁忙，请稍后重试");
}
```

### 3. 日志规范

#### 日志级别
- **ERROR**: 系统错误、需要立即处理
- **WARN**: 警告信息、可预期但需要注意
- **INFO**: 关键业务流程、状态变化
- **DEBUG**: 调试信息、详细执行过程

#### 日志格式
```java
// 错误日志（包含异常堆栈）
log.error("Handle exception, uri:{} =>", requestUri, exception);

// 业务日志
log.info("订单创建成功，orderId: {}, userId: {}", orderId, userId);

// 调试日志
log.debug("SQL: {}, params: {}", sql, params);
```

#### 敏感信息处理
```java
// 错误：直接记录密码
log.info("用户登录成功，username: {}, password: {}", username, password);

// 正确：脱敏处理
log.info("用户登录成功，username: {}, password: {}", username, "******");
```

### 4. 事务管理

#### 事务注解
```java
@Service
public class OrderService {

    @Transactional(rollbackFor = Exception.class)
    public void createOrder(Order order) {
        // 保存订单
        orderMapper.insert(order);

        // 扣减库存
        inventoryService.deduct(order.getProductId(), order.getQuantity());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logOperation(Order order) {
        // 独立事务，不受外部事务回滚影响
        operationLogMapper.insert(log);
    }
}
```

 #### 多数据源 + 事务
```java
@UseDataSource("master")
@Transactional(rollbackFor = Exception.class)
public void saveOrder(Order order) {
    // 主库事务
}

@UseDataSource("slave")
@Transactional(readOnly = true)
public List<Order> queryOrders() {
    // 从库只读事务
}
```

### 5. 数据校验

#### Bean Validation
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
```

#### Controller参数校验
```java
@PostMapping("/order")
public boolean saveOrder(@RequestBody @Valid OrderDTO orderDTO) {
    return orderService.save(orderDTO);
}
```

#### 业务校验
```java
public void createOrder(Order order) {
    AssertUtil.isNotNull(order, "订单不能为空");
    AssertUtil.isNotNull(order.getUserId(), "用户ID不能为空");
    AssertUtil.isTrue(order.getAmount() > 0, "订单金额必须大于0");
}
```

### 6. 分页查询

#### PageHelper分页
```java
@Service
public class UserService {
    public Page<User> pageUsers(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> list = userMapper.selectAll();
        return new PageInfo<>(list);
    }
}
```

#### MyBatis-Flex分页
```java
@Service
public class UserService {
    public Page<User> pageUsers(Page<User> page) {
        return userMapper.paginate(page, new QueryWrapper());
    }
}
```

### 7. 缓存使用

#### Spring Cache
```java
@Service
@CacheConfig(cacheNames = "user")
public class UserService {

    @Cacheable(key = "#id")
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    @CachePut(key = "#user.id")
    public User update(User user) {
        userMapper.updateById(user);
        return user;
    }

    @CacheEvict(key = "#id")
    public void delete(Long id) {
        userMapper.deleteById(id);
    }
}
```

### 8. 异步处理

#### @Async注解
```java
@Service
public class NotificationService {

    @Async("asyncExecutor")
    public void sendEmail(String to, String subject, String content) {
        // 异步发送邮件
    }
}

// 调用
notificationService.sendEmail("user@example.com", "通知", "内容");
```

### 9. 配置管理

#### 配置类
```java
@Data
@Configuration
@ConfigurationProperties(prefix = "uni-boot.payment")
public class PaymentProperties {
    private String appId;
    private String appSecret;
    private int timeout = 5000;
    private List<String> supportTypes = Arrays.asList("alipay", "wechat");
}
```

#### 配置文件
```yaml
uni-boot:
  payment:
    app-id: your-app-id
    app-secret: your-app-secret
    timeout: 3000
    support-types:
      - alipay
      - wechat
      - unionpay
```

### 10. 接口设计

#### RESTful风格
```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping
    public Long save(@RequestBody UserDTO userDTO) {
        return userService.save(userDTO);
    }

    @PutMapping("/{id}")
    public Boolean update(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return userService.update(id, userDTO);
    }

    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Long id) {
        return userService.delete(id);
    }

    @GetMapping
    public Page<User> page(Page<User> page) {
        return userService.page(page);
    }
}
```

#### Swagger注解
```java
@Tag(name = "用户管理", description = "用户相关接口")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Operation(summary = "根据ID查询用户", description = "返回用户详细信息")
    @Parameter(name = "id", description = "用户ID", required = true)
    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @Operation(summary = "保存用户")
    @PostMapping
    public Long save(@RequestBody @Valid UserDTO userDTO) {
        return userService.save(userDTO);
    }
}
```

---

## 总结

uni-boot-template 采用了现代化的分层架构和设计模式，提供了：

1. **清晰的模块划分**: dependency → common → starter → framework → server
2. **强大的扩展机制**: Pipeline + Plugin 双扩展点
3. **完善的基础设施**: 统一异常、统一响应、动态数据源
4. **最佳实践支持**: 命名规范、日志规范、事务管理等
5. **开箱即用**: 代码生成、API文档、监控等

开发者可以基于此模板快速构建企业级应用，同时保持代码的可维护性和可扩展性。
