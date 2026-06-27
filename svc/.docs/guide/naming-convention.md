# 命名约定 (Naming Convention)

本文档描述 uni-boot-template 项目的命名规范，包括通用命名原则、各层命名规范、实体类命名、工具类命名等。

## 目录

- [通用命名原则](#通用命名原则)
- [各层命名规范](#各层命名规范)
- [方法命名规范](#方法命名规范)
- [变量命名规范](#变量命名规范)
- [实体类命名](#实体类命名)
  - [数据库实体布尔变量命名](#数据库实体布尔变量命名)
- [工具类命名](#工具类命名)
- [包结构命名](#包结构命名)
- [常量和枚举命名](#常量和枚举命名)

---

## 通用命名原则

1. **使用英文单词，严禁使用拼音**
2. **使用有意义的名称，避免缩写（除非是公认的缩写，如 id, url, api）**
3. **遵循 Java 官方命名规范**
4. **命名应体现职责和用途**

### 示例对比

```java
// ❌ 错误示例
String yhm = "admin";        // 使用拼音
String u = new User();        // 无意义名称
class UC { }                   // 不合理的缩写

// ✅ 正确示例
String username = "admin";     // 英文单词
String user = new User();      // 有意义
class UserController { }       // 清晰的名称
```

---

## 各层命名规范

### Web 和 Service 层

| 含义描述 | 命名模式 | 包名 | 示例 |
|---------|---------|------|------|
| Web 接口层 | `xxxController` | `controller` | `UserController` |
| Service 接口 | `xxxService` | `service` | `UserService` |
| Service 实现 | `xxxServiceImpl` | `service.impl` | `UserServiceImpl` |
| DAO 数据访问层 | `xxxMapper` | `mapper` | `UserMapper` |
| Manager 复杂业务聚合处理 | `xxxManager` | `manager` | `OrderManager` |
| Feign 客户端 | `xxxClient` | `feignclient` | `UserClient` |

### 扩展和切面层

| 含义描述 | 命名模式 | 包名 | 示例 |
|---------|---------|------|------|
| Bean 转换器 | `xxxConvert` | `convert` | `UserConvert` |
| AOP 切面 | `xxxAspect` | `aspect` | `LogAspect` |
| 监听器 | `xxxListener` | `listener` | `BaseEntityListener` |
| 拦截器 | `xxxInterceptor` | `interceptor` | `AuthInterceptor` |
| 配置类 | `xxxConfig` | `config` | `WebMvcConfig` |

### 常量和枚举

| 含义描述 | 命名模式 | 包名 | 示例 |
|---------|---------|------|------|
| 常量类 | `xxxConst` | `consts` | `ApiConst` |
| 枚举类 | `xxxEnum` | `enums` | `UserTypeEnum` |

### 命名示例

```java
// Controller 层
@RestController
public class UserController { }

// Service 层
public interface UserService { }
@Service
public class UserServiceImpl implements UserService { }

// Mapper 层
@Mapper
public interface UserMapper { }

// Manager 层 - 复杂业务聚合
@Service
public class OrderManager { }

// Feign Client
@FeignClient(name = "user-service")
public interface UserClient { }

// 转换器
public interface UserConvert {
    UserVo toVo(UserEntity entity);
}

// 切面
@Aspect
@Component
public class LogAspect { }

// 监听器
@Component
public class OrderEventListener { }

// 拦截器
public class AuthInterceptor implements HandlerInterceptor { }

// 配置类
@Configuration
public class WebMvcConfig { }

// 常量类
public class ApiConst {
    public static final String SUCCESS = "success";
}

// 枚举类
public enum UserTypeEnum {
    ADMIN,
    USER,
    GUEST;
}
```

---

## 方法命名规范

### CRUD 方法命名

| 功能 | 命名模式 | 示例 |
|------|---------|------|
| 查询单个 | `getById`, `getOne`, `queryById` | `getById(Long id)` |
| 查询列表 | `list`, `selectList`, `queryList` | `list(UserQuery query)` |
| 分页查询 | `page`, `selectPage`, `queryPage` | `page(Page<User> page)` |
| 查询数量 | `count`, `selectCount`, `queryCount` | `count(UserQuery query)` |
| 查询是否存在 | `exists`, `checkExists` | `exists(String username)` |
| 新增 | `save`, `insert`, `add` | `save(User user)` |
| 批量新增 | `saveBatch`, `insertBatch` | `saveBatch(List<User> list)` |
| 更新 | `update`, `modify` | `update(User user)` |
| 批量更新 | `updateBatch` | `updateBatch(List<User> list)` |
| 删除 | `remove`, `delete` | `removeById(Long id)` |
| 批量删除 | `removeBatch`, `deleteBatch` | `removeByIds(List<Long> ids)` |

### 业务方法命名

| 功能 | 命名模式 | 示例 |
|------|---------|------|
| 启用/禁用 | `enable`, `disable` | `enableUser(Long id)` |
| 审批 | `approve`, `reject` | `approveOrder(Long orderId)` |
| 验证 | `validate`, `check` | `validateUser(User user)` |
| 处理 | `process`, `handle` | `processOrder(Order order)` |
| 计算 | `calculate`, `compute` | `calculateAmount(Order order)` |
| 转换 | `convert`, `toXxx`, `fromXxx` | `toVo(UserEntity entity)` |
| 查找 | `find`, `search` | `findByName(String name)` |

### 方法命名示例

```java
@Service
public class UserService {

    // 查询方法
    public User getById(Long id) { }
    public List<User> list(UserQuery query) { }
    public Page<User> page(Page<User> page) { }
    public int count(UserQuery query) { }
    public boolean exists(String username) { }

    // 新增方法
    public void save(User user) { }
    public void saveBatch(List<User> users) { }

    // 更新方法
    public void update(User user) { }
    public void updateBatch(List<User> users) { }

    // 删除方法
    public void removeById(Long id) { }
    public void removeByIds(List<Long> ids) { }

    // 业务方法
    public void enableUser(Long id) { }
    public void disableUser(Long id) { }
    public void validateUser(User user) { }
    public UserVo toVo(UserEntity entity) { }
}
```

---

## 变量命名规范

### 布尔变量命名

#### 普通Java对象

使用 `is`、`has`、`can`、`should` 前缀：

```java
// ✅ 正确示例
boolean isActive;
boolean hasPermission;
boolean canDelete;
boolean shouldRetry;
boolean isDeleted;
boolean isAdmin;

// ❌ 错误示例
boolean active;         // 缺少 is 前缀
boolean permission;     // 缺少 has 前缀
boolean delete;         // 缺少 can 前缀
boolean retry;          // 缺少 should 前缀
```

#### 数据库实体类（特别注意）

**数据库实体类中的布尔变量命名需要遵循特殊规范**：

1. **必须使用包装类型 `Boolean`**，而不是基本类型 `boolean`
2. **避免使用 `is` 前缀**，推荐使用 `Flag` 后缀

详见：[数据库实体布尔变量命名](#数据库实体布尔变量命名)

### 集合变量命名

使用复数形式，并在变量名中体现集合类型：

```java
// ✅ 正确示例
List<User> users;
List<User> userList;
Map<String, User> userMap;
Set<String> usernames;
Collection<Order> orders;

// ❌ 错误示例
List<User> user;           // 应该用复数
Map<String, User> map;     // 不够具体
List<String> nameList;      // 应该用 usernames
```

### 临时变量命名

使用描述性名称，避免使用单字母变量（循环变量除外）：

```java
// ✅ 正确示例
String currentUsername = user.getUsername();
Date now = new Date();
List<User> filteredUsers = users.stream()
    .filter(u -> u.isActive())
    .collect(Collectors.toList());

// ❌ 错误示例
String s = user.getUsername();  // s 无意义
Date d = new Date();             // d 无意义
List<User> list = users.stream() // list 不够具体
    .filter(u -> u.isActive())
    .collect(Collectors.toList());
```

### 常量变量命名

使用全大写下划线分隔：

```java
// ✅ 正确示例
public static final String SUCCESS = "success";
public static final int MAX_RETRY_COUNT = 3;
public static final long DEFAULT_TIMEOUT = 5000L;
public static final String DEFAULT_CHARSET = "UTF-8";

// ❌ 错误示例
public static final String Success = "success";        // 首字母大写
public static final int maxRetryCount = 3;           // 小驼峰
public static final long default_timeout = 5000L;      // 小写下划线
```

---

## 实体类命名

### 实体类分层

| 含义描述 | 命名模式 | 包名 | 示例 |
|---------|---------|------|------|
| 数据库实体 | `xxx` | `bean.entity` / `bean.po` | `User` |
| VO 展示层实体 | `xxxVo` | `bean.vo` | `UserVo` |
| DTO 数据传输层实体 | `xxxDto` | `bean.dto` | `UserDto` |
| BO 复杂业务聚合实体 | `xxxBo` | `bean.bo` | `UserBo` |
| Mongo 实体 | `xxxDoc` | `bean.doc` | `UserDoc` |
| Cache 缓存实体 | `xxxCache` | `bean.cache` | `UserCache` |
| ES 实体 | `xxxIndex` | `bean.index` | `UserIndex` |

### 数据库实体布尔变量命名

数据库实体类中的布尔字段需要特别注意：

1. **使用包装类型 `Boolean`**，而不是基本类型 `boolean`
2. **避免 `is` 前缀的布尔变量名**，推荐使用 `Flag` 后缀

#### 原因说明

- **序列化兼容性**: Jackson、FastJSON 等序列化工具可能对 `isXxx` 格式的属性产生映射问题
- **数据库映射**: ORM 框架在映射 `isXxx` 格式时可能出现字段名不一致
- **避免混淆**: `isAdmin` 可能与 `getAdmin()` 方法混淆

#### 命名示例

```java
// ❌ 错误示例
@Data
@TableName("sys_user")
public class User {
    private Long id;
    private boolean isAdmin;           // ❌ 使用基本类型 + is前缀
    private boolean isDeleted;         // ❌ 使用基本类型 + is前缀
}

// ✅ 正确示例
@Data
@TableName("sys_user")
public class User {
    private Long id;
    private Boolean adminFlag;        // ✅ 包装类型 + Flag后缀
    private Boolean deletedFlag;      // ✅ 包装类型 + Flag后缀
    private Boolean enableFlag;       // ✅ 包装类型 + Flag后缀
}
```

#### 字段映射示例

```java
// 数据库表结构
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY,
    username VARCHAR(50),
    admin_flag TINYINT(1) COMMENT '是否管理员',
    deleted_flag TINYINT(1) COMMENT '是否删除'
);

// 实体类
@Data
@TableName("sys_user")
public class User {
    @TableId
    private Long id;
    private String username;

    // ✅ 数据库字段 admin_flag 映射到 adminFlag
    private Boolean adminFlag;

    // ✅ 数据库字段 deleted_flag 映射到 deletedFlag
    private Boolean deletedFlag;
}
```

#### 常见布尔字段命名建议

| 含义 | 推荐命名 | 不推荐命名 |
|------|---------|-----------|
| 是否管理员 | `adminFlag` | `isAdmin` |
| 是否删除 | `deletedFlag` | `isDeleted` |
| 是否启用 | `enableFlag` | `isEnabled` |
| 是否激活 | `activeFlag` | `isActive` |
| 是否锁定 | `lockedFlag` | `isLocked` |
| 是否验证 | `verifiedFlag` | `isVerified` |
| 是否VIP | `vipFlag` | `isVip` |

### 实体类使用场景

```java
// VO (View Object) - 接口返回实体
// 用于接口返回，使用 Vo 结尾
public class SysUserPageVo {
    private Long id;
    private String username;
    private String nickname;
    // VO 中可以使用 isXxx，不影响序列化
    private Boolean isAdmin;
}

// DTO (Data Transfer Object) - 接口接收实体
// 用于接口接收参数，使用 Dto 结尾
public class SysUserPageDto {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String username;
}

// BO (Business Object) - 业务实体
// 微服务架构下，服务间传递对象使用 Bo 结尾
public class OrderBo {
    private Order order;
    private List<OrderItem> items;
    private User user;
}

// Entity - 数据库实体
// 对应数据库表，直接使用表名
@Data
@TableName("sys_user")
public class User {
    @TableId
    private Long id;
    private String username;
    private String password;

    // ✅ 数据库实体中使用 Boolean + Flag
    private Boolean adminFlag;
    private Boolean deletedFlag;
    private Boolean enableFlag;
}
```

### 消息和事件命名

```java
// 消息实体：使用 Msg 结尾
public class OrderCreateMsg {
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
}

// 事件实体：使用 Event 结尾
public class OrderCreatedEvent extends ApplicationEvent {
    private Long orderId;
}
```

### 特殊实体

一些独立于业务之外的实体，由于工具类、框架配置等的需要，可放于 `model` 包，也可使用内部类方式：

```java
// 方式1：放于 model 包
package com.github.cadecode.uniboot.starter.web.model;

public class ApiResult<T> {
    private Integer status;
    private T data;
    private ErrorMessage error;
}

// 方式2：使用内部类
public class JacksonUtil {
    public static class Holder {
        private static ObjectMapper mapper;
    }
}
```

---

## 工具类命名

### 命名规范

工具类放于 `util` 包：

- **静态工具类**：使用单数的 `Util` 结尾
- **需要注入容器的工具类**：推荐以 `Kit` 结尾进行区分

```java
// 静态工具类 - Util 结尾
public class JacksonUtil {
    public static String toJson(Object obj) { }
    public static <T> T toBean(String json, Class<T> clazz) { }
}

// 需要注入的工具类 - Kit 结尾
@Slf4j
@Component
public class GenCodeKit implements InitializingBean {

    @Autowired
    private DataSource dataSource;

    // 既有静态方法，又有实例方法
    public void codegen(...) { }

    @Override
    public void afterPropertiesSet() { }
}
```

### 工具类示例

| 工具类 | 命名 | 类型 | 说明 |
|--------|------|------|------|
| JSON 处理 | `JacksonUtil` | Util | 静态工具类 |
| XML 处理 | `JacksonXUtil` | Util | 静态工具类 |
| Spring 容器操作 | `SpringUtil` | Util | 静态工具类 |
| 断言工具 | `AssertUtil` | Util | 静态工具类 |
| 树结构转换 | `TreeUtil` | Util | 静态工具类 |
| 代码生成 | `GenCodeKit` | Kit | 需要注入 |
| 数据库文档生成 | `DbDocKit` | Kit | 需要注入 |

---

## 包结构命名

### 标准包结构

```
com.github.cadecode.uniboot.{module}
├── controller/          # Web 接口层
├── service/             # Service 接口
│   └── impl/           # Service 实现
├── mapper/              # DAO 层
├── bean/               # 实体类
│   ├── entity/         # 数据库实体
│   ├── vo/             # 视图对象
│   ├── dto/            # 数据传输对象
│   ├── bo/             # 业务对象
│   └── convert/        # 对象转换器
├── manager/            # 复杂业务聚合处理
├── aspect/             # AOP 切面
├── config/             # 配置类
├── consts/             # 常量
├── enums/              # 枚举
├── util/               # 工具类
├── listener/           # 监听器
└── interceptor/        # 拦截器
```

### 示例：admin 模块包结构

```
com.github.cadecode.uniboot.admin
├── AdminApplication.java
├── controller/
│   ├── UserController.java
│   └── OrderController.java
├── service/
│   ├── UserService.java
│   ├── OrderService.java
│   └── impl/
│       ├── UserServiceImpl.java
│       └── OrderServiceImpl.java
├── mapper/
│   ├── UserMapper.java
│   └── OrderMapper.java
├── bean/
│   ├── entity/
│   │   ├── User.java
│   │   └── Order.java
│   ├── vo/
│   │   ├── UserVo.java
│   │   └── OrderVo.java
│   ├── dto/
│   │   ├── UserDto.java
│   │   └── OrderDto.java
│   └── convert/
│       └── UserConvert.java
└── pipeline/            # 扩展示例
    ├── TestContext.java
    ├── TestFilter1.java
    └── TestFilter2.java
```

### 包命名规则

```java
// ✅ 正确示例
package com.github.cadecode.uniboot.admin.controller;
package com.github.cadecode.uniboot.admin.service.impl;
package com.github.cadecode.uniboot.admin.bean.entity;
package com.github.cadecode.uniboot.admin.bean.vo;

// ❌ 错误示例
package com.github.cadecode.uniboot.admin.Controllers;  // 复数
package com.github.cadecode.uniboot.admin.Service;   // 单词错误
package com.github.cadecode.uniboot.admin.Bean;      // 大写
```

---

## 常量和枚举命名

### 常量命名

```java
// 常量放于 consts 包，命名使用全大写下划线分隔
package com.github.cadecode.uniboot.common.consts;

public class ApiConst {
    public static final String SUCCESS = "success";
    public static final String FAIL = "fail";

    public static final int STATUS_OK = 200;
    public static final int STATUS_BAD_REQUEST = 400;
    public static final int STATUS_NOT_FOUND = 404;
    public static final int STATUS_SERVER_ERROR = 500;

    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final long DEFAULT_TIMEOUT = 5000L;
    public static final int MAX_RETRY_COUNT = 3;
}
```

### 枚举命名

```java
// 枚举放于 enums 包，命名使用 Enum 结尾
package com.github.cadecode.uniboot.common.enums;

@Getter
@AllArgsConstructor
public enum UserTypeEnum implements ErrorCode {
    ADMIN("1001", "管理员"),
    USER("1002", "普通用户"),
    GUEST("1003", "访客");

    private final String code;
    private final String message;
}

@Getter
@AllArgsConstructor
public enum OrderStatusEnum {
    PENDING("0", "待支付"),
    PAID("1", "已支付"),
    SHIPPED("2", "已发货"),
    COMPLETED("3", "已完成"),
    CANCELLED("4", "已取消");

    private final String code;
    private final String desc;
}
```

### 异常类命名

```java
// 基础异常
public class BaseException extends RuntimeException { }

// 通用异常
public class GeneralException extends BaseException { }

// 扩展异常
public class ExtensionException extends GeneralException { }
public class RateLimitException extends GeneralException { }
public class HelperException extends GeneralException { }
public class DynamicDsException extends RuntimeException { }
```

---

## 总结

遵循本命名约定可以保证代码：

1. **可读性强**: 统一的命名规范使代码易于理解
2. **可维护性好**: 清晰的命名规范使代码易于维护
3. **可扩展性高**: 规范的命名设计使功能易于扩展
4. **团队协作顺畅**: 统一的规范降低团队协作成本

### 命名检查清单

在提交代码前，请检查：

- [ ] 类名是否使用大驼峰（PascalCase）？
- [ ] 方法名和变量名是否使用小驼峰（camelCase）？
- [ ] 常量名是否使用全大写下划线分隔？
- [ ] 布尔变量是否使用 is/has/can/should 前缀？
- [ ] **数据库实体中的布尔字段是否使用 `Boolean` 类型 + `Flag` 后缀？**
- [ ] 集合变量是否使用复数形式？
- [ ] 是否避免了拼音和不合理的缩写？
- [ ] 包名是否全部小写且使用点分隔？
- [ ] 是否遵循了各层的命名规范？

建议在开发过程中定期 Review 代码，确保遵循本命名约定。
