<script setup>
// Серверная пагинация: API использует нумерацию страниц с нуля.
defineProps({
  page: { type: Number, default: 0 },
  size: { type: Number, default: 20 },
  totalPages: { type: Number, default: 0 },
  totalElements: { type: Number, default: 0 },
  pending: Boolean,
})

defineEmits(['change', 'size-change'])
</script>

<template>
  <nav v-if="totalPages > 0" aria-label="Пагинация">
    <button :disabled="pending || page <= 0" @click="$emit('change', page - 1)">
      Назад
    </button>
    <span>
      Страница {{ page + 1 }} из {{ totalPages }} · {{ totalElements }} записей
    </span>
    <button
      :disabled="pending || page + 1 >= totalPages"
      @click="$emit('change', page + 1)"
    >
      Далее
    </button>
    <label>
      Размер
      <select
        :value="size"
        :disabled="pending"
        @change="$emit('size-change', Number($event.target.value))"
      >
        <option v-for="n in [10, 20, 50, 100]" :key="n" :value="n">
          {{ n }}
        </option>
      </select>
    </label>
  </nav>
  <p v-else>Записей: {{ totalElements }}</p>
</template>
