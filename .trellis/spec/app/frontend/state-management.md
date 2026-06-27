# State Management

> Pinia store conventions for global shared state in Vue 3.

---

## Pinia Store Pattern

### File Organization

```
stores/
├── userStore.ts
├── cartStore.ts
└── productStore.ts
```

One store per domain — no monolithic store.

---

## Store Template

```typescript
// stores/userStore.ts
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    name: '',
    isLoggedIn: false
  }),

  getters: {
    displayName: (state) => state.name || 'Guest'
  },

  actions: {
    async login(email: string, password: string) {
      // Login logic — call service here
      const result = await userService.login({ email, password })
      this.name = result.name
      this.isLoggedIn = true
    },
    logout() {
      this.$reset()
    }
  }
})
```

---

## Using Stores in Components

```html
<script setup lang="ts">
import { storeToRefs } from 'pinia'
import { useUserStore } from '@/stores/userStore'

const userStore = useUserStore()

// State: use storeToRefs to preserve reactivity
const { name, displayName } = storeToRefs(userStore)

// Actions: destructure directly
const { login, logout } = userStore
</script>
```

> **Critical**: Use `storeToRefs()` for state properties (keeps reactivity). Actions can be destructured directly.

---

## State Categories Decision Flow

```
Is this state needed by ONLY this component?
  → YES: ref() / reactive() in component
  → NO:  Is it server data (from API)?
           → YES: composable with ref() (useUserList)
           → NO:  Is it needed across many pages?
                    → YES: Pinia store
                    → NO:  Parent-child props/emits
```

---

## Store vs Composable

| Tool | When to Use |
|------|------------|
| `ref()` / `reactive()` | Local component state |
| Composable (`useXxx`) | Shared logic + state for a subtree |
| Pinia store | Global state across many pages/routes |

---

## Forbidden Patterns

- ❌ Do NOT destructure Pinia state without `storeToRefs` — loses reactivity
- ❌ Do NOT put all state in a single monolithic store
- ❌ Do NOT call services directly from components — use composables or store actions
- ❌ Do NOT store server cache data in Pinia — use composable-level caching
