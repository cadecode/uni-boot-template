# Frontend Development Guidelines

> Vue 3 + TypeScript + Element Plus conventions for the `app/` package.

---

## Overview

Vue 3 Composition API (`<script setup lang="ts">`) with TypeScript, Pinia state management, and Element Plus component library.

---

## Pre-Development Checklist

Before writing frontend code, review:

- [ ] [Directory Structure](./directory-structure.md) — Vue 3 project layout, file naming (Base/Biz/App prefixes)
- [ ] [Component Guidelines](./component-guidelines.md) — SFC structure, template rules, Element Plus wrapping patterns
- [ ] [Composable Guidelines](./hook-guidelines.md) — `use` prefix, reactive state return, service delegation
- [ ] [State Management](./state-management.md) — Pinia stores, `storeToRefs` usage
- [ ] [Type Safety](./type-safety.md) — TypeScript conventions, `type` imports, API types
- [ ] [Quality Guidelines](./quality-guidelines.md) — Vitest testing, ESLint/Prettier, BEM styles

---

## Guidelines Index

| Guide | Status |
|-------|--------|
| [Directory Structure](./directory-structure.md) | ✅ Filled |
| [Component Guidelines](./component-guidelines.md) | ✅ Filled |
| [Composable Guidelines](./hook-guidelines.md) | ✅ Filled |
| [State Management](./state-management.md) | ✅ Filled |
| [Type Safety](./type-safety.md) | ✅ Filled |
| [Quality Guidelines](./quality-guidelines.md) | ✅ Filled |

---

## Quality Check (for trellis-check)

- TypeScript: `vue-tsc --noEmit` passes, no `any`, `type` keyword on type-only imports
- Component: `<script setup lang="ts">`, props typed with `defineProps<T>()`, `inheritAttrs: false` when wrapping
- Template: custom components PascalCase, Element Plus kebab-case
- Store: `storeToRefs()` for state, actions destructured directly
- Service: API calls only in `services/`, axios instance in `services/api/client.ts`
- Axios interceptors: centralized in `services/api/interceptors.ts`
- Styles: `<style scoped lang="scss">`, BEM naming
- Tests: `.spec.ts` mirrors `src/` structure
- Imports: types → 3rd-party → components → utils/composables
- No `console.log`, no commented-out code

---

**Language**: All documentation in English.
