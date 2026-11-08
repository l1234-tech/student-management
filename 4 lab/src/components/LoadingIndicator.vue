<!-- Назначение: доступный индикатор ожидания сетевой операции. -->
<script setup>
defineProps({ active: Boolean })
</script>

<template>
  <p v-if="active" role="status" aria-live="polite">Загрузка…</p>
</template>
