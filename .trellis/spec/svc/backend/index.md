# Backend Development Guidelines

> Best practices for backend development in this project.

---

## Overview

Java 17 + Spring Boot 3.2.5 + MyBatis-Flex. Multi-module Maven project under `svc/`.

---

## Pre-Development Checklist

Before writing backend code, review:

- [ ] [Directory Structure](./directory-structure.md) — module layout, package conventions
- [ ] [Database Guidelines](./database-guidelines.md) — ORM, entities, type handlers, multi-datasource
- [ ] [Error Handling](./error-handling.md) — exception hierarchy, ApiResult, global handlers
- [ ] [Logging Guidelines](./logging-guidelines.md) — SLF4J usage, log levels, sensitive data
- [ ] [Quality Guidelines](./quality-guidelines.md) — naming, validation, API design, Git commits

---

## Guidelines Index

| Guide | Status |
|-------|--------|
| [Directory Structure](./directory-structure.md) | ✅ Filled |
| [Database Guidelines](./database-guidelines.md) | ✅ Filled |
| [Error Handling](./error-handling.md) | ✅ Filled |
| [Logging Guidelines](./logging-guidelines.md) | ✅ Filled |
| [Quality Guidelines](./quality-guidelines.md) | ✅ Filled |

---

## Quality Check (for trellis-check)

- File location matches the module+package conventions in `directory-structure.md`
- Exception handling: `GeneralException.of(...)`, not raw RuntimeException
- Logging: SLF4J `{}` placeholders, no `System.out`
- Database entities: `Boolean` + `Flag` suffix, no `isXxx`/`boolean`
- API responses: wrapped in `ApiResult<T>`, annotated `@ApiFormat` where needed
- Transactions: `@Transactional(rollbackFor = Exception.class)`
- Commit message: Angular convention `<type>(<scope>): <subject>`

---

**Language**: All documentation in English.
