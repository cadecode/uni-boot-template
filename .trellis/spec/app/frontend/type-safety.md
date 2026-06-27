# Type Safety

> TypeScript conventions for Vue 3 — type organization, imports, and API types.

---

## Type Organization

```
types/
├── User.ts             # User domain types
├── Product.ts          # Product domain types
├── api.ts              # Generic API types
└── global.d.ts         # Global type declarations
```

One file per domain — no single `types.ts` mega-file.

---

## Interface vs Enum vs String Union

```typescript
// types/User.ts

// ✅ Interface for object shapes
export interface UserInfo {
  id: string
  name: string
  email: string
  role: UserRole
}

// ✅ Enum for fixed value sets (when values need import)
export enum UserRole {
  Admin = 'admin',
  User = 'user'
}
```

---

## API Response Types

Mirror backend `ApiResult<T>`:

```typescript
// types/api.ts
export interface ApiResponse<T = unknown> {
  code: number
  message: string
  data: T
}
```

---

## Type Import Convention

```typescript
// ✅ Correct: use 'type' keyword for pure type imports
import type { UserInfo } from '@/types/User'

// ✅ Correct: enums need value import
import { UserRole } from '@/types/User'

// ✅ Correct: combined import
import { type UserInfo, UserRole } from '@/types/User'
```

> Key: Use `type` keyword for type-only imports — helps bundler tree-shaking.

---

## Component Props Typing

```typescript
// ✅ Vue 3.3+ defineProps with generic
interface Props {
  userId: string
  title?: string
}
const props = withDefaults(defineProps<Props>(), {
  title: 'Default'
})
```

---

## Composable Return Types

```typescript
interface UseUserListReturn {
  users: Ref<UserInfo[]>
  loading: Ref<boolean>
  error: Ref<string | null>
  refetch: () => Promise<void>
}

export function useUserList(): UseUserListReturn {
  // ...
}
```

---

## Forbidden Patterns

- ❌ Do NOT use `any` — use `unknown` if type is truly uncertain
- ❌ Do NOT use `as` type assertions without narrowing first
- ❌ Do NOT put all types in one `types.ts` file — split by domain
- ❌ Do NOT forget `type` keyword on pure type imports
- ❌ Do NOT export raw API responses as component props — define domain DTOs
