# Component Guidelines

> Vue 3 SFC structure, component naming, and template conventions.

---

## Single File Component Structure

Strict import and code ordering within `<script setup lang="ts">`:

```html
<script setup lang="ts">
// 1. Type imports
import type { UserInfo } from '@/types/User'

// 2. Third-party libraries
import { ElMessage } from 'element-plus'

// 3. Component imports
import BaseButton from '@/components/common/BaseButton.vue'

// 4. Utils / composables
import { useUser } from '@/composables/useUser'

// 5. Props
interface Props {
  userId: string
  title?: string
}
const props = withDefaults(defineProps<Props>(), {
  title: 'Default'
})

// 6. Emits
const emit = defineEmits<{
  (e: 'update', user: UserInfo): void
}>()

// 7. Reactive state
const loading = ref(false)

// 8. Computed
const displayName = computed(() => user.value?.name)

// 9. Methods
async function fetchUser() { }

// 10. Lifecycle
onMounted(() => { })

// 11. Expose
defineExpose({ refresh: fetchUser })
</script>

<template>
  <!-- Custom components: PascalCase; third-party: kebab-case -->
  <BaseButton @click="handleClick" />
  <el-button type="primary" />
</template>

<style scoped lang="scss">
/* BEM naming */
.user-card {
  &__header { }
  &--active { }
}
</style>
```

---

## Template Usage Rules

| Component Type | Template Style | Example |
|----------------|---------------|---------|
| Custom components | PascalCase | `<BaseButton />` |
| Third-party UI (Element Plus) | kebab-case | `<el-button />` |
| Native HTML | lowercase | `<div />` |

---

## Component Naming by Type

| Type | Prefix | Example |
|------|--------|---------|
| Generic UI (no business logic) | `Base` | `BaseButton`, `BaseInput`, `BaseModal` |
| Business (cross-page) | `Biz` | `BizUserCard`, `BizOrderTable` |
| Layout | `App` | `AppHeader`, `AppSidebar`, `AppFooter` |
| Page-scoped | (none) | `UserFilter`, `OrderDetail` |

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

> Key: `use` prefix, returns reactive state and methods.

---

## Component Library Wrapping (Element Plus)

When wrapping third-party components, follow this pattern to preserve ref access and slot passthrough:

### Template-based wrapping

```html
<script lang="ts" setup>
import type { InputInstance, InputProps } from 'element-plus'
import { getCurrentInstance } from 'vue'
import type { ExtractPropTypes } from 'vue'

// 1. Props type (inherits all Element Plus Input props)
export interface CustomInputProps extends ExtractPropTypes<InputProps> {
  title?: string  // Custom prop
}

// 2. Instance type (inherits original + custom methods)
export interface CustomInputInstance extends InputInstance {
  someClick: () => void
}

defineOptions({ inheritAttrs: false })

// 3. Props defaults
const props = withDefaults(defineProps<CustomInputProps>(), {
  title: 'Custom Input',
  clearable: true
})

// 4. Events
const emit = defineEmits<{ (e: 'titleClick'): void }>()

const vm = getCurrentInstance()

// 5. ref callback: merge original instance + custom methods
function changeRef(inputInstance: Record<string, any> | null) {
  if (vm) {
    vm.exposeProxy = vm.exposed = Object.assign(inputInstance || {}, { someClick })
  }
}

defineExpose((vm?.exposeProxy || {}) as CustomInputInstance)
</script>

<template>
  <div class="custom-input">
    <div @click="handleTitleClick">{{ title }}</div>
    <!-- Passthrough all attrs and slots -->
    <el-input :ref="changeRef" v-bind="{ ...$attrs, ...props }">
      <template v-for="(_, name) in $slots" :key="name" #[name]="slotProps">
        <slot :name="name" v-bind="slotProps" />
      </template>
    </el-input>
  </div>
</template>
```

### h-function wrapping (alternative for slot passthrough)

```html
<script lang="ts" setup>
import { h } from 'vue'
import { ElInput } from 'element-plus'
// ... (same props/emits/expose as above)
</script>

<template>
  <div class="custom-input">
    <div @click="handleTitleClick">{{ title }}</div>
    <!-- h() auto-handles slot passthrough -->
    <component :is="h(ElInput, { ...$attrs, ...props, ref: changeRef }, $slots)" />
  </div>
</template>
```

---

## Forbidden Patterns

- ❌ Do NOT use Options API — use `<script setup lang="ts">` (Composition API)
- ❌ Do NOT skip `defineOptions({ inheritAttrs: false })` when wrapping components
- ❌ Do NOT use `any` for props — use `ExtractPropTypes<>` from the wrapped library
- ❌ Do NOT forget to passthrough `$slots` when wrapping third-party components
