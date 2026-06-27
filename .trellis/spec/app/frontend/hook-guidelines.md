# Composable Guidelines

> Vue 3 composable naming, patterns, and conventions (Vue's equivalent of React hooks).

---

## Composable Naming

- Always `use` prefix: `useUser`, `useCounter`, `useDebounce`
- File: `useCamelCase.ts` in `composables/`
- Name describes what it DOES: `useUserList` > `useFetchUsers`

---

## Composable Template

```typescript
// composables/useCounter.ts
export function useCounter(initialValue = 0) {
  const count = ref(initialValue)
  const doubled = computed(() => count.value * 2)

  function increment() {
    count.value++
  }

  function reset() {
    count.value = initialValue
  }

  return { count, doubled, increment, reset }
}
```

> Key principles: `use` prefix, returns reactive state (`ref`/`computed`) and methods.

---

## Data Fetching Composable Pattern

```typescript
// composables/useUserList.ts
import type { UserInfo } from '@/types/User'
import { userService } from '@/services/userService'

export function useUserList() {
  const users = ref<UserInfo[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function fetchUsers(params: Record<string, any>) {
    loading.value = true
    error.value = null
    try {
      users.value = await userService.list(params)
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Unknown error'
    } finally {
      loading.value = false
    }
  }

  // Auto-fetch on creation
  fetchUsers({})

  return { users, loading, error, refetch: fetchUsers }
}
```

---

## Usage in Components

```html
<script setup lang="ts">
import { useUserList } from '@/composables/useUserList'

const { users, loading, error, refetch } = useUserList()
</script>
```

---

## Separation of Concerns

Composables should delegate API calls to `services/`, not call `fetch`/`axios` directly:

```
component → composable → service → axios
```

- **Component**: renders UI, handles events
- **Composable**: manages reactive state + lifecycle
- **Service**: only API calls, no UI logic

---

## Forbidden Patterns

- ❌ Do NOT call composables inside conditions/loops
- ❌ Do NOT call `fetch`/`axios` directly in composables — use `services/`
- ❌ Do NOT put composables in `utils/` — use `composables/`
- ❌ Do NOT return raw API responses from composables — type them
