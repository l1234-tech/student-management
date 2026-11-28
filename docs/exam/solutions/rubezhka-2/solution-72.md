# 72. Напишите код маршрута /students/:id и компонента страницы студента на выбранном SPA-фреймворке. Получите id из URL, загрузите GET /api/students/{id}, покажите состояние загрузки, карточку либо сообщение «Не найдено» при 404.

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

~~~ts
import { createRouter, createWebHistory } from "vue-router";
import StudentPage from "./StudentPage.vue";

export const router = createRouter({
  history: createWebHistory(),
  routes: [{ path: "/students/:id", component: StudentPage }]
});
~~~

~~~vue
<script setup>
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();
const student = ref(null);
const loading = ref(true);
const notFound = ref(false);
const error = ref("");

onMounted(async () => {
  try {
    const response = await fetch(
      "/api/students/" + encodeURIComponent(route.params.id)
    );
    if (response.status === 404) {
      notFound.value = true;
      return;
    }
    if (!response.ok) throw new Error("HTTP " + response.status);
    student.value = await response.json();
  } catch {
    error.value = "Не удалось загрузить студента";
  } finally {
    loading.value = false;
  }
});
</script>
<template>
  <p v-if="loading">Загрузка...</p>
  <p v-else-if="notFound">Не найдено</p>
  <p v-else-if="error">{{ error }}</p>
  <article v-else-if="student">{{ student.name }} — {{ student.group }}</article>
</template>
~~~

:id — параметр маршрута. useRoute читает URL; onMounted запускает GET в браузере. student хранит JSON, loading показывает ожидание, notFound отличает HTTP 404, error — другие сбои. finally выполняется в любом исходе. Это CSR-экран Not Found; при SSR надо также выставить HTTP статус 404. Если id может быстро меняться, наблюдают за параметром и отменяют устаревший запрос.
