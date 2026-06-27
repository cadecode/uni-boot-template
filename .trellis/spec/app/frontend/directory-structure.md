# Frontend Directory Structure

> Vue 3 + TypeScript project layout for the `app/` package.

---

## Project Structure

```
app/
├── src/
│   ├── assets/                      # Static assets (images, fonts)
│   ├── components/
│   │   ├── common/                  # Generic UI components (reusable, no business logic)
│   │   │   └── BaseButton.vue
│   │   └── biz/                     # Business components (reusable, with business logic)
│   │       └── BizUserCard.vue
│   ├── composables/                 # Composable functions (reusable stateful logic)
│   │   └── useUser.ts
│   ├── layouts/                     # Layout components (page-level structure)
│   │   ├── default.vue
│   │   └── components/              # Layout-only components
│   │       └── AppHeader.vue
│   ├── pages/                       # Page components (route targets, kebab-case)
│   │   ├── user-profile.vue
│   │   └── user/                    # Page module
│   │       ├── list.vue
│   │       └── components/          # Page-scoped components
│   │           └── UserFilter.vue
│   ├── router/                      # Route configuration
│   │   ├── index.ts
│   │   └── user-routes.ts
│   ├── stores/                      # Pinia state management (global shared state)
│   │   └── userStore.ts
│   ├── services/                    # API service layer (encapsulates backend calls)
│   │   ├── userService.ts
│   │   └── api/
│   │       ├── client.ts           # Axios instance config
│   │       └── interceptors.ts     # Request/response interceptors
│   ├── utils/                       # Utility functions (pure, no side effects)
│   │   ├── formatDate.ts
│   │   └── validateEmail.ts
│   ├── types/                       # TypeScript type definitions
│   │   ├── User.ts
│   │   └── api.ts
│   ├── styles/                      # Global styles (variables, mixins, reset)
│   │   └── global.scss
│   ├── App.vue
│   └── main.ts
├── tests/
│   └── unit/                        # Unit tests (mirrors src/ structure)
│       ├── components/
│       └── utils/
└── package.json
```

---

## File Naming Rules

| Type | Rule | Example | Notes |
|------|------|---------|-------|
| Generic UI component | `Base` + PascalCase | `BaseButton.vue` | No business logic, pure UI |
| Biz component | `Biz` + PascalCase | `BizUserCard.vue` | Cross-page reuse, with business logic |
| Layout component | `App` + PascalCase | `AppHeader.vue` | Layout-only |
| Page file | kebab-case | `user-profile.vue` | Matches route path |
| Page-scoped component | PascalCase | `UserFilter.vue` | Only for current module |
| Composable | `use` + camelCase | `useUser.ts` | Logic reuse |
| Store | camelCase + `Store` | `userStore.ts` | Global state |
| Service | camelCase + `Service` | `userService.ts` | API encapsulation |
| Utility | camelCase | `formatDate.ts` | Pure function |
| Type definition | PascalCase | `User.ts` | Interface/enum |
| Test file | sourceName + `.spec.ts` | `BaseButton.spec.ts` | Unit test |

---

## Component Organization by Type

| Component Type | Directory | Naming Rule | Example |
|----------------|-----------|-------------|---------|
| Generic UI | `components/common/` | `Base` + PascalCase | `BaseButton.vue` |
| Business | `components/biz/` | `Biz` + PascalCase | `BizUserCard.vue` |
| Layout | `layouts/components/` | `App` + PascalCase | `AppHeader.vue` |
| Page-scoped | `pages/xxx/components/` | PascalCase | `UserFilter.vue` |

---

## Forbidden Patterns

- ❌ Do NOT mix page and reusable components — `pages/` vs `components/`
- ❌ Do NOT create a single `utils.ts` mega-file — one file per function
- ❌ Do NOT put API calls directly in components — use `services/`
- ❌ Do NOT place composables in `utils/` — use `composables/`
