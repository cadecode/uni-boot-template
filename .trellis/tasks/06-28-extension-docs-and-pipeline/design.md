# Design: Extension Pipeline 编排注册方案

## 架构概览

```
┌─────────────────────────────────────────────────────┐
│  PipelineExecutor (API entry)                       │
│    accept(context) → PipelineGenerator               │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│  PipelineRegistry (cache & lookup)                  │
│    getGenerator(type) → PipelineGenerator<ctx>      │
│    // Map<String, PipelineGenerator>                 │
│    @PostConstruct:                                  │
│      for each type in YAML:                         │
│        find filters where supports(type) == true     │
│        validate YAML names ⊆ matched                │
│        build chain in YAML list order               │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│  YAML Config Source (唯一顺序来源)                   │
│  uni-boot.extension.pipeline.filter-selectors       │
│    ORDER: [saveFilter, queryFilter, checkFilter]    │
└─────────────────────────────────────────────────────┘
```

## 组件设计

### 1. `PipelineFilter` 新增 `supports()` 方法

对标 `PluginService extends Plugin<PluginContext>`（继承自 Spring Plugin 的 `supports()`）：

```java
// PipelineFilter 接口新增方法
public interface PipelineFilter<T extends PipelineContext> {
    /**
     * 声明此 filter 支持哪些 type
     * 对标 Plugin.supports(delimiter)
     */
    boolean supports(ExtensionType type);

    void doFilter(T context, PipelineFilterChain<T> filterChain);
}
```

```java
// AbstractPipelineFilter 提供便捷默认实现
public abstract class AbstractPipelineFilter<T extends PipelineContext> 
    implements PipelineFilter<T> {

    /**
     * 简单场景：覆盖此方法返回支持的 type 名集合
     */
    protected Set<String> getSupportedTypes() {
        return Collections.emptySet();
    }

    @Override
    public boolean supports(ExtensionType type) {
        return getSupportedTypes().contains(type.getType());
    }

    /**
     * 复杂场景：直接覆盖 supports() 做任意逻辑判断
     * （如根据 type 的 category、上下文条件等）
     */

    // --- doFilter 模板保持不变 ---
    @Override
    public void doFilter(T context, PipelineFilterChain<T> filterChain) {
        if (context.getFilterSelector().matchFilter(this.getClass().getSimpleName())) {
            handle(context);
        }
        if (context.continueChain()) {
            filterChain.next(context);
        }
    }

    public abstract void handle(T context);
}
```

**使用示例**：

```java
@Component
public class TestFilter1 extends AbstractPipelineFilter<TestContext> {
    @Override
    protected Set<String> getSupportedTypes() {
        return Set.of("BIZ1", "BIZ2");  // 声明归属
    }

    @Override
    public void handle(TestContext context) {
        System.out.println("test1");
    }
}
```

**与 Plugin 模式的一致性**：

| | Plugin | Pipeline |
|------|--------|----------|
| 匹配声明 | `Plugin.supports(PluginContext)` | `PipelineFilter.supports(ExtensionType)` |
| 注册机制 | Spring Plugin Registry | `PipelineRegistry`（自定义） |
| 执行器 | `PluginSelectorExecutor` | `PipelineExecutor` |
| 策略选择 | 运行时 `filter(o -> o.supports(context))` | 启动时预构建 chain，运行时直接执行 |

**与 demo 项目差异**：demo 用 `@EventFilterAnnotation(bizCode="...", priority=N)` 注解声明归属+排序；我们去掉注解，用 `supports(type)` 声明归属，YAML 单独负责排序。职责分离更清晰，且与项目内 Plugin 包风格一致。

### 2. YAML 配置结构

```yaml
uni-boot:
  extension:
    pipeline:
      enabled: true
      filter-selectors:
        ORDER:           # ExtensionType.getType() 值
          - saveOrderFilter
          - queryOrderFilter
          - checkOrderFilter
          - userPayWayFilter
        CHARGE:
          - logSaveFilter
          - addressInfoQueryFilter
```

bean name 列表顺序即执行顺序，无额外 priority/order 字段。

### 3. `PipelineRegistry`

```java
@Component
public class PipelineRegistry {
    private final Map<String, PipelineGenerator<?>> generatorCache = new HashMap<>();
    private final List<PipelineFilter<?>> allFilters;      // Spring 注入所有 filter beans
    private final PipelineProperties properties;

    @PostConstruct
    public void init() {
        Map<String, List<String>> selectors = properties.getFilterSelectors();
        if (selectors == null) return;

        for (Map.Entry<String, List<String>> entry : selectors.entrySet()) {
            String typeName = entry.getKey();
            List<String> beanNames = entry.getValue();

            // 1. 找到所有 supports(type) == true 的 filter
            ExtensionType type = () -> typeName;
            List<PipelineFilter<?>> matched = allFilters.stream()
                    .filter(f -> f.supports(type))
                    .toList();

            // 2. 校验 YAML 中的 bean name 都在匹配列表中
            for (String name : beanNames) {
                if (matched.stream().noneMatch(f -> matchesBeanName(f, name))) {
                    log.warn("YAML filter '{}' not found or doesn't support type '{}'", name, typeName);
                }
            }

            // 3. 按 YAML 顺序构建 PipelineGenerator
            PipelineGenerator<Object> gen = new PipelineGenerator<>();
            for (String name : beanNames) {
                matched.stream()
                    .filter(f -> matchesBeanName(f, name))
                    .findFirst()
                    .ifPresent(f -> gen.appendFilter((PipelineFilter<Object>) f));
            }
            generatorCache.put(typeName, gen);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends PipelineContext> PipelineGenerator<T> getGenerator(ExtensionType type) {
        return (PipelineGenerator<T>) generatorCache.getOrDefault(
            type.getType(), PipelineGenerator::new);
    }
}
```

**校验规则**：
- YAML 配置了 type，但没有任何 filter 的 `supports(type)` 返回 true → warn 日志 + 空 chain
- YAML 中某个 bean name 对应的 bean 存在但 `supports(type)` 返回 false → warn 日志 + 跳过该项

### 4. `PipelineExecutor`

```java
@Component
public class PipelineExecutor {
    private final PipelineRegistry registry;

    public <T extends PipelineContext> void accept(T context) {
        PipelineGenerator<T> gen = registry.getGenerator(context.getPipelineType());
        gen.getFirstChain().filter(context);
    }
}
```

### 5. `MatchAllFilterSelector`

stash 中已有，保留原样：
```java
public class MatchAllFilterSelector implements FilterSelector {
    @Override public boolean matchFilter(String name) { return true; }
    @Override public List<String> getFilterNames() { return null; }
}
```

## 数据流

```
1. App starts
2. Spring scans @Component → all PipelineFilter beans 注入 PipelineRegistry.allFilters
3. PipelineRegistry @PostConstruct:
   a. For each YAML-configured type:
      - Create stub ExtensionType, call f.supports(type) on all filters
      - Collect matched filters
   b. Validate YAML bean names ⊆ matched filters (warn on missing)
   c. Build PipelineGenerator per type using YAML list order (唯一顺序来源)
   d. Cache in generatorCache (Map<String, PipelineGenerator>)
4. Business code:
   a. Create context with ExtensionType + FilterSelector (e.g. MatchAllFilterSelector)
   b. Call pipelineExecutor.accept(context)
   c. → registry looks up generator by context.getPipelineType()
   d. → firstChain.filter(context) starts chain execution
```

## 向后兼容

- `PipelineGenerator.appendFilter()` 保持 public API 不变
- `PipelineContext` / `PipelineFilterChain` / `DefaultPipelineFilterChain` 不变
- `PipelineFilter` 接口新增 `supports()` 方法，但所有 Concrete Filter 通过 `AbstractPipelineFilter` 继承默认实现，无需改动
- `AbstractPipelineFilter` 增加默认 `supports()` 实现（基于 `getSupportedTypes()`），现有 filter 不覆盖则返回 false（不匹配任何 type）
- `PipelineRegistry` 作为增强层叠加，不修改核心抽象

## 文件结构

```
common/src/main/java/.../extension/pipeline/
├── PipelineContext.java              (existing)
├── AbstractPipelineContext.java      (existing)
├── PipelineFilter.java               (existing) ← 新增 supports(ExtensionType)
├── AbstractPipelineFilter.java       (existing) ← 新增 getSupportedTypes() + supports() 默认实现
├── PipelineFilterChain.java          (existing)
├── DefaultPipelineFilterChain.java   (existing)
├── PipelineGenerator.java            (existing)
├── selector/
│   ├── FilterSelector.java          (existing)
│   ├── LocalListFilterSelector.java (existing)
│   └── MatchAllFilterSelector.java  (new, from stash)
├── config/
│   ├── PipelineProperties.java      (new, from stash)
│   └── PipelineAutoConfig.java      (new, enhanced)
├── PipelineRegistry.java            (new)
└── PipelineExecutor.java            (new, enhanced from stash)
```

> 不再需要 `annotation/PipelineExtension.java`，注解方案已被 `supports()` 替代。

## 关键设计决策

| 决策 | 理由 |
|------|------|
| `supports(type)` 替代注解声明归属 | 与 Plugin 包一致（`Plugin.supports(delimiter)`），支持复杂逻辑判断 |
| YAML 为启用/禁用来源 | `supports()` 声明归属，`appendFilter` 顺序即执行顺序；YAML 仅控制启用哪些 filter |
| 默认不在 `AbstractPipelineFilter.doFilter` 中过滤 | 保持 filter 纯逻辑；编排层（YAML + chain）决定是否启用 |
| 不做远程配置（本期） | 远程配置（Nacos/Apollo）留待后续，YAML 结构本身兼容远程下发 |
| 不做缓存（本期） | `@PostConstruct` 一次性构建，已是常驻 |
| 不做异步（本期） | `PipelineExecutor` 后续可扩展 `acceptAsync` |
