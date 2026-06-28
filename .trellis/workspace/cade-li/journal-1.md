# Journal - cade-li (Part 1)

> AI development session journal
> Started: 2026-06-28

---



## Session 1: Bootstrap Trellis monorepo specs from existing docs

**Date**: 2026-06-28
**Task**: Bootstrap Trellis monorepo specs from existing docs
**Branch**: `dev`

### Summary

Converted to monorepo (svc=backend, app=frontend). Extracted coding conventions from svc/AGENTS.md, naming-convention.md, git-commit.md, and app/ naming-convention.md into .trellis/spec/. Filled 11 spec files (5 backend with real code examples, 6 frontend with Vue 3 conventions). Deep-audited and corrected all specs against actual codebase patterns.

### Main Changes

(Add details)

### Git Commits

| Hash | Message |
|------|---------|
| `9857c7e` | (see git log) |
| `1894d48` | (see git log) |

### Testing

- [OK] (Add test results)

### Status

[OK] **Completed**

### Next Steps

- None - task complete


## Session 2: Pipeline 配置下沉 + README/Spec 优化

**Date**: 2026-06-28
**Task**: Pipeline 配置下沉 + README/Spec 优化
**Branch**: `dev`

### Summary

Pipeline 重构：PipelineGenerator→PipelineExecutor + FilterSelector 体系，FilterSelectorFactory 静态工具，配置下沉到业务层（admin PipelineTestProperties），YmlConfigFilterSelector 移除（与 LocalListFilterSelector 重复），15 tests 通过。README 修复陈旧 API 引用与重复代码，Spec 示例同步。

### Main Changes

(Add details)

### Git Commits

| Hash | Message |
|------|---------|
| `7b8eebf` | (see git log) |
| `f68eddc` | (see git log) |
| `20d5b52` | (see git log) |
| `f107e1c` | (see git log) |
| `0df7105` | (see git log) |
| `8d8a78f` | (see git log) |
| `3c856fe` | (see git log) |
| `d8e68e1` | (see git log) |

### Testing

- [OK] (Add test results)

### Status

[OK] **Completed**

### Next Steps

- None - task complete
