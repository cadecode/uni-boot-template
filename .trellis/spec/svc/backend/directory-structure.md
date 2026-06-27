# Backend Directory Structure

> Module organization, package layout, and file placement conventions.

---

## Multi-Module Architecture

The backend (`svc/`) follows a strict layered Maven multi-module architecture with unidirectional dependencies:

```
dependency (BOM) → common → starter → framework → server
                                     ↘ codegen
```

| Module | Role | Depends On |
|--------|------|------------|
| `dependency` | BOM — centralized dependency version management | nothing |
| `common` | Foundation — shared utils, exceptions, extension points | dependency |
| `starter` | Auto-configuration — pluggable starters (web, swagger, mybatis, datasource) | common |
| `framework` | Integration — wires all starters into a cohesive config layer | starter |
| `codegen` | Code generation — MyBatis-Flex-based generator utilities | common |
| `server` | Application — business modules (admin) | framework |

**Key principle**: upper layers depend on lower layers; lower layers never depend on upper layers.

---

## Module Internal Package Structure

Each business module follows this standard layout:

```
com.github.cadecode.uniboot.<module>/
├── controller/          # REST controllers (Spring MVC @RestController)
├── service/             # Service interfaces
│   └── impl/           # Service implementations (@Service)
├── mapper/              # MyBatis-Flex Mapper interfaces
├── bean/               # Data objects
│   ├── entity/         # Database entities (PO, maps to table)
│   ├── vo/             # View objects (API response)
│   ├── dto/            # Data transfer objects (API request)
│   ├── bo/             # Business objects (cross-service aggregation)
│   └── convert/        # Object converters (MapStruct mappers)
├── manager/            # Complex business aggregation handlers
├── aspect/             # AOP aspects
├── config/             # @Configuration classes
├── consts/             # Constants
├── enums/              # Enumerations
├── util/               # Utility classes
├── listener/           # Event listeners
└── interceptor/        # Request interceptors
```

### Package Naming Rules

- All lowercase, dot-separated
- No plurals: `controller`, not `controllers`
- Base package: `com.github.cadecode.uniboot.<module>`

Real example — `svc/server/admin/`:

```
svc/server/admin/src/main/java/com/github/cadecode/uniboot/admin/
├── AdminApplication.java
├── controller/
├── service/
│   └── impl/
├── mapper/
├── bean/
│   ├── entity/
│   ├── vo/
│   └── dto/
└── pipeline/            # Pipeline extension examples
    ├── TestContext.java
    ├── TestFilter1.java
    └── TestFilter2.java
```

---

## Starter Module Organization

Each starter is a standalone Maven module under `svc/starter/`:

```
svc/starter/
├── pom.xml       (parent POM for all starters)
├── web/          — unified response, global exception handling
├── swagger/      — Knife4j API docs auto-configuration
├── mybatis/      — MyBatis-Flex config, BaseEntity, type handlers
├── datasource/   — dynamic datasource switching (AOP-based)
└── actuator/     — Spring Boot Actuator monitoring
```

### Starter Naming Convention

- Module directory: `starter/<name>/` (lowercase, hyphen-free)
- Source package: `com.github.cadecode.uniboot.starter.<name>/`
- Auto-config class: `<Name>AutoConfig.java` (e.g. `SwaggerAutoConfig.java`)
- Properties class: `<Name>Properties.java` (e.g. `SwaggerProperties.java`)

---

## Common Module Organization

```
svc/common/src/main/java/com/github/cadecode/uniboot/common/
├── consts/              # Constant definitions (ApiStatus)
├── enums/               # Enums (ErrorCode interface, ExtensionType)
├── exception/           # Exception hierarchy
│   ├── BaseException.java
│   ├── GeneralException.java
│   ├── ExtensionException.java
│   ├── RateLimitException.java
│   └── HelperException.java
├── extension/           # Extension point framework
│   ├── pipeline/        # Pipeline (Chain of Responsibility)
│   │   ├── filter/
│   │   └── selector/
│   └── plugin/          # Plugin (Strategy)
│       └── config/
└── util/                # Static utility classes
    ├── AssertUtil.java
    ├── JacksonUtil.java
    ├── JacksonXUtil.java
    ├── SpringUtil.java
    └── TreeUtil.java
```

---

## Config File Locations

| File | Location |
|------|----------|
| Root POM | `svc/pom.xml` |
| BOM | `svc/dependency/pom.xml` |
| Module POMs | `svc/<module>/pom.xml` |
| Application config | `svc/server/admin/src/main/resources/application.yml` |
| Environment config | `svc/server/admin/src/main/resources/application-{dev,test,prod}.yml` |
| Codegen config | `svc/codegen/src/main/resources/application.yml` |

---

## Forbidden Patterns

- ❌ Do NOT put business logic in `controller` — it belongs in `service`
- ❌ Do NOT depend upward (e.g. `common` depending on `starter`)
- ❌ Do NOT use plural package names (`controllers`, `services`)
- ❌ Do NOT place entity classes directly under `bean/` — use `bean/entity/`
- ❌ Do NOT mix static utils and Spring-injected utils in the same class (use `Util` vs `Kit` suffix)
