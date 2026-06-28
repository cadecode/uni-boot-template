# PRD: Extension 工具链文档化与 Pipeline 注册方案

## 背景

`svc/common/.../extension/` 下已封装了两套设计模式工具：

| 工具 | 模式 | 基础 |
|------|------|------|
| **Pipeline** | 责任链（Chain of Responsibility） | 自研，Filter 链表 |
| **Plugin** | 策略模式（Strategy） | Spring Plugin Framework |

当前问题：
1. 只有一份设计模式教学文稿（README.md），不是实际工具的使用文档
2. Pipeline 注册方式原始：手动 `new PipelineGenerator().appendFilter(filter)`，缺少编排层
3. Spec 中仅有接口描述，没有使用指南

## 目标

### 1. 分析现有 Extension 工具 ✅
确认 Pipeline/Plugin 的角色定义、接口契约、调用链路，形成架构认知。

### 2. 生成 Extension 使用 README（doc-coauthoring skill）
产出面向开发者的使用指南，包含：
- Pipeline 使用方式（手工编排 + 基于 selector 的启用控制）
- Plugin 使用方式（Spring Plugin 注册 + 执行器调用）
- 典型业务场景示例（如订单下单 Pipeline）

### 3. 更新 Trellis Spec
将 Pipeline/Plugin 的用法、约定、示例写入 `.trellis/spec/svc/backend/` 下的相关文件。

### 4. Pipeline 编排注册方案
设计一套通过 **YAML 配置 + `supports()` 方法** 来编排 Pipeline filter 顺序与启用的方案，替代纯手动拼接：

| 特性 | 说明 |
|------|------|
| `PipelineFilter.supports(ExtensionType)` | 接口方法声明 filter 支持的业务类型（对标 Plugin 的 `supports()`） |
| `AbstractPipelineFilter.getSupportedTypes()` | 便捷默认实现，返回 `Set<String>` 即可 |
| YAML `uni-boot.extension.pipeline.filter-selectors` | 每个 type 的 filter bean name 列表（顺序即执行顺序） |
| `PipelineRegistry` | 启动时遍历 YAML type，调用 `supports(type)` 收集匹配 filter，按 YAML 顺序构建 PipelineGenerator |
| `PipelineAutoConfig` | Spring Boot 自动配置，注册 PipelineRegistry + PipelineExecutor bean |
| `PipelineExecutor` | 执行器，根据 context.getPipelineType() 获取预编排好的 PipelineGenerator 并执行 |
| `MatchAllFilterSelector` | 默认 selector，无需过滤时用（from stash） |
| 可选增强 | 参考 demo 项目：优先级(priority)、缓存、异步 |

## 范围

- **In scope**: `svc/common/.../extension/` 源码分析、README 生成、Spec 更新、Pipeline 注册方案设计与实现
- **Out of scope**: 修改 Plugin 部分（现有实现已完善）、前端代码、远程配置中心集成、`@PipelineExtension` 注解（已被 `supports()` 替代）
- **参考**: git stash 内容（当前 WIP）、`D:\project-space\fork\pipeline-demo` 项目

## 验收标准

1. Extension README 能独立指导开发者使用 Pipeline + Plugin 工具
2. Spec 中新增/更新 extension 相关章节，有代码示例
3. Pipeline 可通过 YAML 配置 filter 启用/禁用，通过 FilterSelectorFactory 创建 selector
4. 15 个测试用例覆盖编程式和声明式两种 API，全部通过
5. 不破坏现有 `PipelineGenerator.appendFilter` 编程式 API（向后兼容）
