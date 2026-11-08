<script setup>
// Контролируемое поле поиска по точному ФИО.
defineProps({
  modelValue: {
    type: String,
    default: '',
  },
})

defineEmits(['update:modelValue'])
</script>

<template>
  <label>
    ФИО
    <input
      type="search"
      :value="modelValue"
      placeholder="Точное совпадение"
      @input="$emit('update:modelValue', $event.target.value)"
    />
  </label>
</template>
