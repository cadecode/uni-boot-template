# Extension 工具使用指南

`svc/common/.../extension/` 封装了两套设计模式工具，用于构建可扩展的业务逻辑。

| 工具 | 模式 | 基础 | 适用场景 |
|------|------|------|---------|
| **Pipeline** | 责任链（Chain of Responsibility） | 自研 Filter 链表 | 流程化逻辑拆分，多个处理单元按序执行 |
| **Plugin** | 策略模式（Strategy） | Spring Plugin Framework | 多种算法/策略动态切换 |

---

## 1. Pipeline（责任链）

### 1.1 角色定义

| 角色 | 接口/类 | 职责 |
|------|---------|------|
| 上下文 | `PipelineContext` | 封装请求参数、输出模型、业务类型、FilterSelector |
| 上下文抽象 | `AbstractPipelineContext` | 提供 `pipelineType` + `filterSelector` 构造注入 |
| 过滤器 | `PipelineFilter<T>` | 处理单元：`doFilter(context, chain)` |
| 过滤器抽象 | `AbstractPipelineFilter<T>` | 模板方法：Selector 匹配 → `handle()` → chain 传递 |
| 过滤器链 | `PipelineFilterChain` | 链表接口：`filter()` + `next()` |
| 过滤器链实现 | `DefaultPipelineFilterChain` | 链表节点，持有当前 filter + 下一节点指针 |
| 管道构建器 | `PipelineGenerator` | `appendFilter()` 构建链表，返回头节点 |
| 过滤器选择器 | `FilterSelector` | 运行时决定哪些 filter 生效：`matchFilter(name)` |
| 业务类型 | `ExtensionType` | Marker 接口：`getType()` 返回业务标识 |

### 1.2 使用方式

#### 方式一：编程式（手工编排）

适用于 filter 少、不需要动态配置的简单场景。

**Step 1：定义 Context**

```java
// 业务类型枚举
@Getter
public enum OrderCodeEnum implements ExtensionType {
    PLACE_ORDER("ORDER"),
    CANCEL_ORDER("CANCEL");

    private final String type;
    OrderCodeEnum(String type) { this.type = type; }
}

// 上下文
@Getter
@Setter
public class OrderContext extends AbstractPipelineContext {
    private boolean continueChain = true;
    private OrderParam param;
    private OrderModel model;

    public OrderContext(ExtensionType pipelineType, FilterSelector filterSelector) {
        super(pipelineType, filterSelector);
    }

    @Override
    public boolean continueChain() { return continueChain; }
}
```

**Step 2：实现 Filter**

```java
@Component
public class CheckOrderFilter extends AbstractPipelineFilter<OrderContext> {
    @Override
    public void handle(OrderContext context) {
        OrderModel model = context.getModel();
        if (model == null) {
            context.setContinueChain(false);
            return;
        }
        System.out.println("校验通过");
    }
}
```

**Step 3：手工编排执行**

```java
// 构建 Pipeline
PipelineGenerator<OrderContext> generator = new PipelineGenerator<>();
generator.appendFilter(new SaveOrderFilter(), "保存订单");
generator.appendFilter(new QueryOrderFilter(), "查询信息");
generator.appendFilter(new CheckOrderFilter(), "校验订单");

// 构造选择器（选哪些 filter 生效）
LocalListFilterSelector selector = new LocalListFilterSelector();
selector.addFilter("SaveOrderFilter");
selector.addFilter("QueryOrderFilter");
selector.addFilter("CheckOrderFilter");

// 创建上下文并执行
OrderContext context = new OrderContext(OrderCodeEnum.PLACE_ORDER, selector);
context.setParam(orderParam);
generator.getFirstChain().filter(context);
// 获取处理结果
OrderModel model = context.getModel();
```

#### 方式二：YAML 启用控制 ✨ 推荐

开发时固定 filter 顺序，每个业务模块在自己的 YAML 中配置启用哪些 filter。通过 `FilterSelectorFactory` 静态工具解析。

**Step 1：开发时构建 Pipeline（固定顺序）**

```java
// Filter 实现
@Component
public class SaveOrderFilter extends AbstractPipelineFilter<OrderContext> {
    @Override
    public void handle(OrderContext context) {
        System.out.println("保存下单请求");
    }
}
```

```java
// Pipeline 配置：开发时确定 filter 顺序
@Configuration
public class OrderPipelineConfig {
    @Bean
    public PipelineExecutor<OrderContext> orderPipeline(
            SaveOrderFilter saveFilter,
            QueryOrderFilter queryFilter,
            CheckOrderFilter checkFilter) {
        PipelineExecutor<OrderContext> gen = new PipelineExecutor<>();
        gen.appendFilter(saveFilter, "保存订单");    // desc + 固定顺序
        gen.appendFilter(queryFilter, "查询信息");
        gen.appendFilter(checkFilter, "校验订单");
        return gen;
    }
}
```

**Step 2：YAML 配置启用/禁用（业务模块自己的 prefix）**

```yaml
order:
  pipeline:
    filter-selectors:
      ORDER:                        # 只配启用的 filter
        - SaveOrderFilter
        - CheckOrderFilter
        # QueryOrderFilter 未列出 = 禁用
      CHARGE:
        - QueryOrderFilter
```

```java
// 业务模块维护自己的 Properties
@Data
@ConfigurationProperties(prefix = "order.pipeline")
public class OrderPipelineProperties {
    private Map<String, List<String>> filterSelectors;
}
```

> YAML 列表只控制启用/禁用，不控制顺序。顺序由 `PipelineExecutor.appendFilter()` 调用顺序决定。

**Step 3：通过 FilterSelectorFactory 静态方法创建 selector，PipelineExecutor 执行**

```java
@Autowired
private PipelineExecutor<OrderContext> orderPipeline;
@Autowired
private OrderPipelineProperties props;

public void placeOrder(OrderParam param) {
    // 调用 common 的静态工厂方法，传入业务的 filter-selectors
    FilterSelector selector = FilterSelectorFactory.createFilterSelector(
            OrderCodeEnum.PLACE_ORDER.getType(), props.getFilterSelectors());

    OrderContext context = new OrderContext(OrderCodeEnum.PLACE_ORDER, selector);
    context.setParam(param);

    orderPipeline.execute(context);
    OrderModel model = context.getModel();
}
```

| Selector 类型 | YAML 配置情况 | 行为 |
|-------------|------------|------|
| `YmlConfigFilterSelector` | type 在 YAML 中有配置 | 只匹配 YAML 中列出的 filter 名 |
| `MatchAllFilterSelector` | type 配置为 `on` | 全部 filter 生效 |
| `DummyFilterSelector` | type 配置为 `off` | 全部 filter 禁用 |
| — | type 未在 YAML 中配置 | 抛出 ExtensionException |

### 1.3 FilterSelector 说明

| Selector | 行为 | 使用场景 |
|----------|------|---------|
| `LocalListFilterSelector` | `matchFilter(name)` 检查 name 是否在列表中 | 编程式模式，精确控制哪些 filter 生效 |
| `MatchAllFilterSelector` | `matchFilter(name)` 始终返回 true | 无过滤需求时使用 |
| `DummyFilterSelector` | `matchFilter(name)` 始终返回 false | 禁用所有 filter 时使用 |
| `YmlConfigFilterSelector` | 基于 YAML 配置的启用列表 | 声明式模式，由 `FilterSelectorFactory` 创建 |

> `AbstractPipelineFilter.doFilter()` 模板会先调用 `context.getFilterSelector().matchFilter(name)`，只有匹配时才执行 `handle()`。

### 1.4 中断链

在 `handle()` 中调用 `context.setContinueChain(false)` 即可中断后续 filter 执行：

```java
@Override
public void handle(OrderContext context) {
    if (invalid) {
        context.setContinueChain(false);  // 校验不通过，后续 filter 不再执行
        return;
    }
    // 正常处理...
}
```

---

## 2. Plugin（策略模式）

基于 Spring Plugin Framework，通过 `Plugin.supports()` 运行时匹配策略。

### 2.1 角色定义

| 角色 | 接口/类 | 职责 |
|------|---------|------|
| 插件服务 | `PluginService extends Plugin<PluginContext>` | 策略接口，继承 Spring Plugin 的 `supports()` |
| 插件上下文 | `PluginContext` | 携带 `getPluginType()` 供匹配 |
| 业务类型 | `ExtensionType` | 与 Pipeline 共用，`getType()` 返回类型标识 |
| 插件执行器 | `PluginExecutor` | 接口：`execute()` / `executeAll()` / `submit()` / `submitAll()` |
| 选择执行器 | `PluginSelectorExecutor` | 核心实现：遍历 pluginRegistry，`filter(o -> o.supports(context))` 选择策略 |

### 2.2 使用方式

**Step 1：定义 Plugin 接口**

```java
public interface PayPlugin extends PluginService {
    void pay(OrderModel model);
}
```

**Step 2：实现策略**

```java
@Component
public class AliPayPlugin implements PayPlugin {
    @Override
    public boolean supports(PluginContext context) {
        // 运行时判断：是否匹配支付宝支付类型
        return PayTypeEnum.ALIPAY.equals(context.getPluginType());
    }

    @Override
    public void pay(OrderModel model) {
        System.out.println("支付宝支付");
    }

    @Override
    public boolean supports(PluginContext delimiter) {
        return PayTypeEnum.ALIPAY.equals(delimiter.getPluginType());
    }
}

@Component
public class WeChatPayPlugin implements PayPlugin {
    @Override
    public boolean supports(PluginContext context) {
        return PayTypeEnum.WECHAT.equals(context.getPluginType());
    }

    @Override
    public void pay(OrderModel model) {
        System.out.println("微信支付");
    }

    @Override
    public boolean supports(PluginContext delimiter) {
        return PayTypeEnum.WECHAT.equals(delimiter.getPluginType());
    }
}
```

**Step 3：注册 Plugin Registry（在 Application 类上）**

```java
@SpringBootApplication
@EnablePluginRegistries({PayPlugin.class})
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
```

**Step 4：通过 PluginSelectorExecutor 调用**

```java
@Autowired
private PluginSelectorExecutor pluginExecutor;

public void doPay(OrderModel model, PayTypeEnum payType) {
    AdaptContext context = new AdaptContext(payType);  // PluginContext 实现

    // 执行匹配的第一个插件
    pluginExecutor.execute(PayPlugin.class, context, plugin -> {
        plugin.pay(model);
    });

    // 或提交后获取返回值
    String result = pluginExecutor.submit(PayPlugin.class, context,
        PayPlugin::getPayUrl);
}
```

**内部机制**：`PluginSelectorExecutor.selectService()` 遍历 `pluginRegistry` 中所有 bean，调用 `plugin.supports(context)` 找到匹配的插件（引用 `context.getPluginType()` 判断）。

### 2.3 适配两种上下文

---

## 3. Pipeline vs Plugin 选择指南

| 维度 | Pipeline | Plugin |
|------|----------|--------|
| 模式 | 责任链 | 策略 |
| 核心操作 | 多个 filter **依次处理**同一请求 | 选择一个 plugin **执行替代**操作 |
| 输出 | 链式处理，模型逐渐完善 | 策略返回结果 |
| 匹配方式 | YAML 控制启用/禁用 | 运行时 `supports(context)` 动态匹配 |
| 典型场景 | 订单下单流程、审批流 | 支付方式切换、通知渠道选择 |
| 可组合 | ✅ 多个 filter 叠加 | ❌ 通常只选一个 |

---

## 4. API 速查

### Pipeline

```java
// PipelineFilter 接口
public interface PipelineFilter<T extends PipelineContext> {
    void doFilter(T context, PipelineFilterChain<T> chain);
}

// PipelineExecutor 构建 + 执行
PipelineExecutor<T> gen = new PipelineExecutor<>();
gen.appendFilter(filter, "desc");
gen.execute(context);

// FilterSelectorFactory（静态工具）
FilterSelector selector = FilterSelectorFactory.createFilterSelector(
    type.getType(), props.getFilterSelectors());
```

### Plugin

```java
// PluginService 接口
public interface PluginService extends Plugin<PluginContext> { }

// PluginSelectorExecutor 调用
pluginExecutor.execute(PluginClass.class, context, plugin -> { ... });
pluginExecutor.submit(PluginClass.class, context, PluginClass::method);
```

---

## 5. 项目结构

```
common/src/main/java/.../extension/
├── pipeline/
│   ├── PipelineContext.java              # Pipeline 上下文接口
│   ├── AbstractPipelineContext.java      # 上下文抽象实现
│   ├── PipelineFilter.java               # Filter 接口
│   ├── AbstractPipelineFilter.java       # Filter 模板类
│   ├── PipelineFilterChain.java          # Filter 链接口
│   ├── DefaultPipelineFilterChain.java   # Filter 链实现
│   ├── PipelineExecutor.java             # 构建器 + 执行器
│   └── selector/
│       ├── FilterSelector.java           # 选择器接口
│       ├── LocalListFilterSelector.java  # 基于本地列表的选择器
│       ├── YmlConfigFilterSelector.java  # 基于 YAML 启用列表的选择器
│       ├── MatchAllFilterSelector.java   # 全匹配选择器
│       ├── DummyFilterSelector.java      # 全禁用选择器
│       └── FilterSelectorFactory.java    # 静态工具，按 type + Map 创建 Selector
└── plugin/
    ├── PluginService.java                # 插件服务接口
    ├── PluginContext.java                # 插件上下文接口
    ├── PluginExecutor.java               # 插件执行器接口
    ├── AbstractPluginExecutor.java       # 执行器抽象
    ├── PluginSelectorExecutor.java       # 基于 Spring Plugin Registry 的执行器
    └── config/
        └── PluginAutoConfig.java         # 插件自动配置

server/admin/src/main/java/.../admin/
├── config/
│   ├── PipelineTestProperties.java       # 业务模块示例：自己的 YAML Properties
│   └── PipelineTestConfig.java           # 注册 Properties
└── pipeline/                             # Filter 实现示例
    ├── TestFilter1/2/3.java
    ├── TestContext.java
    └── TestType.java
```
