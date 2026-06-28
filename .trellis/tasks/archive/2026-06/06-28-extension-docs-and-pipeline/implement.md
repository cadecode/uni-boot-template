# Implement: Extension 工具链文档化与 Pipeline 注册方案

## 执行顺序

### Phase A: README 生成（doc-coauthoring skill）

- [ ] A1. 使用 doc-coauthoring skill 生成 `svc/common/.../extension/README.md`
  - Pipeline 部分：接口契约、角色说明、使用方式（手工编排 + selector 启用控制）、完整示例
  - Plugin 部分：Spring Plugin 注册、执行器调用模式、插件选择器逻辑
  - 替换现有的设计模式教学文稿
- [ ] A2. 验证：README 可被不熟悉代码的开发者理解

### Phase B: Spec 更新

- [ ] B1. 更新 `.trellis/spec/svc/backend/quality-guidelines.md`
  - 在 Extension Point Patterns 章节下补充 Pipeline 使用示例
  - 补充 Plugin 执行器调用示例
- [ ] B2. 验证：Spec 中示例与实际 API 一致

### Phase C: Pipeline 注册方案实现

- [ ] C1. 实现 `MatchAllFilterSelector`（从 stash 提取）
- [ ] C2. 修改 `PipelineFilter` 接口，新增 `boolean supports(ExtensionType type)` 方法
- [ ] C3. 修改 `AbstractPipelineFilter`，新增 `getSupportedTypes()` + `supports()` 默认实现
- [ ] C4. 实现 `PipelineProperties`（从 stash 提取）
- [ ] C5. 实现 `PipelineAutoConfig`（注册 PipelineRegistry + PipelineExecutor bean）
- [ ] C6. 实现 `PipelineRegistry`
  - 注入所有 `PipelineFilter<?>` beans + `PipelineProperties`
  - `@PostConstruct`：遍历 YAML type，调用 `f.supports(type)` 收集匹配 filter
  - 校验 YAML bean name 在匹配列表中
  - 按 YAML 列表顺序构建 `PipelineGenerator` 并缓存
  - 无 YAML 或 supports 不匹配 → empty generator + warn 日志
- [ ] C7. 实现 `PipelineExecutor`（基于 stash stub 增强）
- [ ] C8. 更新 admin 模块 TestFilter（从 stash 提取，适配 `supports()` 模式），确保新方案可运行
- [ ] C9. 添加 application.yml 的 pipeline 配置段（从 stash 提取）

### Phase D: 验证

- [ ] D1. 编译通过：`mvn compile -pl common` + `mvn compile -pl server/admin`
- [ ] D2. Spec 检查：对照 `.trellis/spec/svc/backend/` 中的所有检查项
- [ ] D3. 手工验证 README 中示例可运行

## 涉及文件

```
新增:
  svc/common/.../extension/pipeline/selector/MatchAllFilterSelector.java
  svc/common/.../extension/pipeline/config/PipelineProperties.java
  svc/common/.../extension/pipeline/config/PipelineAutoConfig.java
  svc/common/.../extension/pipeline/PipelineRegistry.java
  svc/common/.../extension/pipeline/PipelineExecutor.java

修改:
  svc/common/.../extension/pipeline/PipelineFilter.java (新增 supports())
  svc/common/.../extension/pipeline/filter/AbstractPipelineFilter.java (新增 getSupportedTypes() + supports() 默认实现)
  svc/common/.../extension/README.md
  .trellis/spec/svc/backend/quality-guidelines.md

从stash提取:
  svc/server/admin/.../pipline/TestContext.java
  svc/server/admin/.../pipline/TestFilter1.java
  svc/server/admin/.../pipline/TestFilter2.java
  svc/server/admin/.../pipline/TestFilter3.java
  svc/server/admin/src/main/resources/application.yml (追加 pipeline 配置)
```

## 回滚点

- 每个 Phase 完成后 commit，出问题可回退到上一 commit
- `PipelineGenerator.appendFilter` 不受影响，旧代码无需改动
