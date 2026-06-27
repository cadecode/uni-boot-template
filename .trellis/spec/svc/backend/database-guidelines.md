# Database Guidelines

> ORM patterns, entity design, type handlers, and multi-datasource configuration.

---

## ORM Framework

**MyBatis-Flex 1.8.8** with **Druid 1.2.21** connection pool. PageHelper 6.1.0 for pagination.

```yaml
# svc/framework/.../application.yml
mybatis-flex:
  mapper-locations: classpath*:mapper/mysql/**/*.xml
  type-aliases-package: com.github.cadecode.**.bean,com.github.cadecode.**.mybatis.converter
  configuration:
    map-underscore-to-camel-case: true
  global-config:
    key-config:
      key-type: generator
      value: uuid          # UUID primary key strategy
```

---

## Entity Design

### Base Entity Interface

All database entities implement `BaseEntity` for automatic audit field population:

```java
// svc/starter/mybatis/.../model/BaseEntity.java
public interface BaseEntity {
    LocalDateTime getCreateTime();
    void setCreateTime(LocalDateTime dateTime);
    String getCreateUser();
    void setCreateUser(String user);
    LocalDateTime getUpdateTime();
    void setUpdateTime(LocalDateTime dateTime);
    String getUpdateUser();
    void setUpdateUser(String user);

    // Default setter hook — used by SetListener for field-level operations
    default Object set(Object entity, String property, Object value) {
        return value;
    }
}
```

> ⚠️ Audit fields use `LocalDateTime`, not `java.util.Date`.

### Entity Naming Conventions

| Concept | Pattern | Example |
|---------|---------|---------|
| Database entity | `Xxx` (table name) | `User`, `Order` |
| View object | `XxxVo` | `UserVo`, `SysUserPageVo` |
| DTO | `XxxDto` | `UserDto`, `SysUserPageDto` |
| Business object | `XxxBo` | `OrderBo` |
| Mongo document | `XxxDoc` | `UserDoc` |
| Cache object | `XxxCache` | `UserCache` |
| ES index | `XxxIndex` | `UserIndex` |

### Boolean Fields in Database Entities

**CRITICAL**: Use **wrapper type `Boolean`** (not primitive `boolean`) and **`XxxFlag` suffix** (not `isXxx` prefix):

```java
// ✅ Correct
@Data
@TableName("sys_user")
public class User {
    private Long id;
    private Boolean adminFlag;     // NOT boolean isAdmin
    private Boolean deletedFlag;   // NOT boolean isDeleted
    private Boolean enableFlag;    // NOT boolean isEnabled
}

// ❌ Wrong
private boolean isAdmin;   // primitive + is-prefix breaks serialization/ORM mapping
```

**Why**: Jackson/FastJSON serialization issues with `isXxx` getters; ORM mapping inconsistencies.

**Database mapping**:
```sql
CREATE TABLE sys_user (
    admin_flag TINYINT(1) COMMENT '是否管理员',
    deleted_flag TINYINT(1) COMMENT '是否删除'
);
-- Maps to: adminFlag, deletedFlag in Java entity
```

### BaseEntityListener (Auto-Fill Audit Fields)

MyBatis-Flex listener auto-populates `createTime`/`updateTime` on insert/update:

```java
// svc/starter/mybatis/.../listener/BaseEntityListener.java
public class BaseEntityListener implements InsertListener, UpdateListener, SetListener {
    @Override
    public void onInsert(Object entity) {
        if (entity instanceof BaseEntity baseEntity) {
            baseEntity.setCreateTime(LocalDateTime.now());
        }
    }
    @Override
    public void onUpdate(Object entity) {
        if (entity instanceof BaseEntity baseEntity) {
            baseEntity.setUpdateTime(LocalDateTime.now());
        }
    }
}
```

> Uses MyBatis-Flex's `InsertListener`/`UpdateListener`/`SetListener` interfaces — NOT the generic `EntityListener<T>`.

### Registering Listeners (MyBatisFlexConfig)

```java
// svc/starter/mybatis/.../config/MyBatisFlexConfig.java
@Configuration
public class MyBatisFlexConfig {
    @Bean
    public MyBatisFlexCustomizer myBatisFlexCustomizer(BaseEntityListener baseEntityListener) {
        return globalConfig -> {
            globalConfig.registerInsertListener(baseEntityListener, BaseEntity.class);
            globalConfig.registerUpdateListener(baseEntityListener, BaseEntity.class);
            globalConfig.registerSetListener(baseEntityListener, BaseEntity.class);
        };
    }
}
```

---

## Mapper Pattern

Mappers are interfaces in `mapper/` package:

```java
@Mapper  // MyBatis-Flex annotation
public interface UserMapper extends BaseMapper<User> {
    List<User> selectByCondition(QueryWrapper query);
}
```

---

## Type Handlers

### BoolToIntTypeHandler

Maps `Boolean` ↔ `TINYINT(1)` in database:

```java
// svc/starter/mybatis/.../convertor/BoolToIntTypeHandler.java
@MappedTypes({Boolean.class})
@MappedJdbcTypes({JdbcType.INTEGER})
public class BoolToIntTypeHandler extends BaseTypeHandler<Boolean> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Boolean param, JdbcType jt) {
        ps.setInt(i, param ? 1 : 0);
    }
    @Override
    public Boolean getNullableResult(ResultSet rs, String col) throws SQLException {
        return rs.getInt(col) != 0;
    }
}
```

### ObjToStrTypeHandler

Serializes Java objects to JSON string in database:

```java
// svc/starter/mybatis/.../convertor/ObjToStrTypeHandler.java
public class ObjToStrTypeHandler extends BaseTypeHandler<Object> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object param, JdbcType jt) {
        ps.setString(i, JacksonUtil.toJson(param));
    }
    @Override
    public Object getNullableResult(ResultSet rs, String col) throws SQLException {
        return JacksonUtil.toBean(rs.getString(col), Object.class);
    }
}
```

### DbEnumConvertible

Enum-to-database value mapping interface:

```java
// svc/starter/mybatis/.../convertor/DbEnumConvertible.java
public interface DbEnumConvertible {
    int dbValue();  // Integer value stored in database
}
```

### DefaultEnumTypeHandler (Generic Enum Handler)

Automatically handles all enums — uses `dbValue()` if `DbEnumConvertible`, otherwise `ordinal()`:

```java
// svc/starter/mybatis/.../convertor/DefaultEnumTypeHandler.java
// Registered as the default enum type handler:
// mybatis.configuration.default-enum-type-handler=...DefaultEnumTypeHandler
public class DefaultEnumTypeHandler<E extends Enum<?>> extends BaseTypeHandler<E> {
    // Builds internal map at construction time
    // If enum implements DbEnumConvertible → use dbValue() as key
    // Otherwise → use ordinal() as key
}
```

---

## Pagination

### PageHelper Config

```java
// svc/starter/mybatis/.../config/PageHelperConfig.java
@Configuration
public class PageHelperConfig {
    @Bean
    public PageInterceptor pageInterceptor() {
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("reasonable", "true");    // pageNum<=0 → first page
        properties.setProperty("autoRuntimeDialect", "true");  // Auto-detect DB dialect
        // properties.setProperty("page-size-zero", "true");  // pageSize=0 → all (disabled)
        pageInterceptor.setProperties(properties);
        return pageInterceptor;
    }
}
```

### PageHelper Usage

```java
public Page<User> pageUsers(int pageNum, int pageSize) {
    PageHelper.startPage(pageNum, pageSize);
    List<User> list = userMapper.selectAll();
    return new PageInfo<>(list);
}
```

### MyBatis-Flex native pagination

```java
public Page<User> pageUsers(Page<User> page) {
    return userMapper.paginate(page, new QueryWrapper());
}
```

---

## Multi-DataSource

MyBatis-Flex native multi-datasource support (no third-party dependency required).

### Configuration

```yaml
mybatis-flex:
  datasource:
    master:
      type: druid
      url: jdbc:mysql://localhost:3306/master
      username: root
      password: root
    slave1:
      type: com.zaxxer.hikari.HikariDataSource
      url: jdbc:mysql://localhost:3306/slave
      username: root
      password: root
```

### Annotation-Based Switching

`@UseDataSource` on Mapper, Service, Controller class or method:

```java
@UseDataSource("slave1")
public interface UserMapper {
    List<User> queryUsers();
}

@Service
public class UserService {
    @UseDataSource("master")
    public void saveUser(User user) { ... }

    @UseDataSource("slave1")
    public List<User> listUsers() { ... }
}
```

### Programmatic Switching

```java
// Manual try-finally
try {
    DataSourceKey.use("slave2");
    // queries here
} finally {
    DataSourceKey.clear();
}

// Lambda-style auto-cleanup
List<User> users = DataSourceKey.use("slave2", () -> userMapper.selectAll());
```

### Read/Write Splitting

Custom `DataSourceShardingStrategy`:

```java
public class MyShardingStrategy implements DataSourceShardingStrategy {
    @Override
    public String doSharding(String currentKey, Object mapper, Method method, Object[] args) {
        String name = method.getName();
        if (StringUtil.startWithAny(name, "insert", "delete", "update")) {
            return "master";
        }
        return "slave" + RandomUtil.randomInt(1, 3);
    }
}
```

---

## Transaction Management

```java
@Service
public class OrderService {
    @Transactional(rollbackFor = Exception.class)
    public void createOrder(Order order) {
        orderMapper.insert(order);
        inventoryService.deduct(order.getProductId(), order.getQuantity());
    }

    // Independent transaction — not affected by outer rollback
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logOperation(Order order) {
        operationLogMapper.insert(log);
    }
}
```

**Multi-datasource + transaction**: `@UseDataSource` and `@Transactional` can coexist on the same method.

---

## Forbidden Patterns

- ❌ Do NOT use primitive `boolean` for entity fields — always `Boolean` + `Flag` suffix
- ❌ Do NOT use `isXxx` naming for database entity boolean fields
- ❌ Do NOT manage DataSourceKey without `finally` cleanup — prefer lambda form
- ❌ Do NOT use raw JDBC — use MyBatis-Flex Mapper or QueryWrapper
