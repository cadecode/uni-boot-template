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
- Tests mirror source under `src/test/java`
- Resources follow profile naming: `application-{dev,test,prod}.yml`
- BaseEntity uses `LocalDateTime`, not `java.util.Date`
- BaseEntityListener implements `InsertListener`/`UpdateListener`/`SetListener` — NOT `EntityListener<T>`
- Enums for DB: implement `DbEnumConvertible` with `dbValue()` returning `int`
- Enums for request params: implement `ParamEnumConvertible` with `paramValue()`
- DB boolean fields: `Boolean` + `Flag` suffix, no `isXxx`/`boolean`
- Exception handling: `GeneralException.of(...)`, not raw RuntimeException
- ErrorCode enums override `getStatus()` per constant via anonymous class
- ApiResultAdvisor handles String body (JSON wrap) and null body (RES_BODY_NULL)
- Logging: SLF4J `{}` placeholders, no `System.out`, exception as last arg
- API responses: wrapped in `ApiResult<T>` via `@ApiFormat` + `ResponseBodyAdvice`
- Starters: `@ConditionalOnMissingBean` on all `@Bean` methods
- Transactions: `@Transactional(rollbackFor = Exception.class)`
- Thread pool: `@EnableAsync` + `@EnableScheduling` on config
- Commit message: Angular convention `<type>(<scope>): <subject>`

---

**Language**: All documentation in English.
