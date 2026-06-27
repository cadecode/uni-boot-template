# Frontend Quality Guidelines

> Testing, toolchain, code style, and pre-commit standards for Vue 3 + TypeScript.

---

## Toolchain (Expected)

| Tool | Purpose |
|------|---------|
| Vue Test Utils + Vitest | Component & unit testing |
| ESLint + @vue/eslint-config-typescript | Static analysis |
| Prettier | Code formatting |
| TypeScript (`vue-tsc --noEmit`) | Type checking |

---

## Testing Conventions

### File Organization

Tests mirror `src/` directory structure:

```
tests/unit/
├── components/
│   ├── common/
│   │   └── BaseButton.spec.ts
│   └── biz/
│       └── BizUserCard.spec.ts
├── composables/
│   └── useCounter.spec.ts
├── stores/
│   └── userStore.spec.ts
├── services/
│   └── userService.spec.ts
└── utils/
    └── formatDate.spec.ts
```

### Component Test

```typescript
// tests/unit/components/common/BaseButton.spec.ts
import { mount } from '@vue/test-utils'
import BaseButton from '@/components/common/BaseButton.vue'

describe('BaseButton', () => {
  it('renders slot content', () => {
    const wrapper = mount(BaseButton, {
      slots: { default: 'Click me' }
    })
    expect(wrapper.text()).toBe('Click me')
  })

  it('emits click event', async () => {
    const wrapper = mount(BaseButton)
    await wrapper.trigger('click')
    expect(wrapper.emitted('click')).toBeTruthy()
  })
})
```

### Composable Test

```typescript
// tests/unit/composables/useCounter.spec.ts
import { useCounter } from '@/composables/useCounter'

describe('useCounter', () => {
  it('initializes with default value', () => {
    const { count } = useCounter()
    expect(count.value).toBe(0)
  })

  it('increments correctly', () => {
    const { count, increment } = useCounter(5)
    increment()
    expect(count.value).toBe(6)
  })
})
```

### Utility Test

```typescript
// tests/unit/utils/formatDate.spec.ts
import { formatDate } from '@/utils/formatDate'

describe('formatDate', () => {
  it('formats with default pattern', () => {
    const date = new Date(2024, 0, 15)  // Jan 15, 2024
    expect(formatDate(date)).toBe('2024-01-15')
  })

  it('formats with custom pattern', () => {
    const date = new Date(2024, 0, 15)
    expect(formatDate(date, 'YYYY/MM/DD')).toBe('2024/01/15')
  })
})
```

---

## Utility Function Conventions

Pure functions, no side effects, one file per function:

```typescript
// utils/formatDate.ts
export function formatDate(date: Date, format = 'YYYY-MM-DD'): string {
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return format.replace('YYYY', String(year)).replace('MM', month).replace('DD', day)
}
```

> Key: one file per function — never create a `utils.ts` mega-file.

---

## Axios Interceptor Pattern

Centralized request/response handling in `services/api/`:

```typescript
// services/api/client.ts
import axios from 'axios'

export const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
})

// services/api/interceptors.ts
import { request } from './client'
import { ElMessage } from 'element-plus'

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

request.interceptors.response.use(
  (response) => response.data,  // Unwrap to data directly
  (error) => {
    const msg = error.response?.data?.error?.message || '请求失败'
    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

// services/userService.ts — uses the configured client
import { request } from './api/client'
import type { UserInfo } from '@/types/User'

export const userService = {
  async getUser(id: string): Promise<UserInfo> {
    const { data } = await request.get(`/api/users/${id}`)
    return data
  },
}
```

> Key: Only `services/` calls axios. Components and composables call services, never raw HTTP.

---

## Styles

- Scoped styles: `<style scoped lang="scss">`
- BEM naming convention:
  ```scss
  .user-card {
    &__header { }   // Element
    &--active { }   // Modifier
  }
  ```

---

## Pre-Commit Checklist

- [ ] `vue-tsc --noEmit` passes (type check)
- [ ] `eslint` passes
- [ ] `prettier --check` passes
- [ ] `vitest run` passes (when tests exist)
- [ ] No `console.log` left in production code
- [ ] No commented-out code
- [ ] Imports follow order: types → 3rd-party → components → utils/composables

---

## Forbidden Patterns

- ❌ Do NOT commit `console.log` — remove or use proper logger
- ❌ Do NOT commit commented-out code — delete it (git history exists)
- ❌ Do NOT create a `utils.ts` mega-file — one file per function
- ❌ Do NOT skip `defineOptions({ inheritAttrs: false })` when wrapping components
- ❌ Do NOT use Options API — use `<script setup lang="ts">`
- ❌ Do NOT destructure Pinia state without `storeToRefs()`
- ❌ Do NOT call `fetch`/`axios` directly in components — use services
- ❌ Do NOT use `any` — use `unknown` or proper types
