<!-- Назначение: страница 403 для авторизованного пользователя без нужной роли. -->
<script setup>
import { RouterLink } from 'vue-router'
</script>

<template>
  <section>
    <h2>403 — доступ запрещён</h2>
    <p>У вашей учётной записи недостаточно прав для этой операции.</p>
    <RouterLink to="/students">Вернуться к списку студентов</RouterLink>
  </section>
</template>
