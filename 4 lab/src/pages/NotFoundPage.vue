<script setup>
// Назначение: неизвестный frontend URL; вход — catch-all маршрута, выход — ссылка к списку.
// TODO: При необходимости вывести route.fullPath интерполяцией, без запросов.
// 404 GET /api/user/students/{isu} обрабатывает StudentPage для USER/ADMIN, это другое состояние.
// Взаимодействует с router/index.js; не принимает DTO/роли и не обращается к API.
// Проверка: /unknown, ссылка к списку, reload вложенного URL через Nginx.
import { RouterLink } from 'vue-router'
</script>

<template>
  <section>
    <h2>404 — страница не найдена</h2>
    <RouterLink to="/students">К списку</RouterLink>
  </section>
</template>
