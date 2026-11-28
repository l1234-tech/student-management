# 74. Напишите код ленивой загрузки страницы ReportsPage по маршруту /reports (например, React.lazy/Suspense или эквивалент выбранного фреймворка). Пока код страницы загружается, отображайте текст «Загрузка отчётов».

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

~~~ts
import { createRouter, createWebHistory } from "vue-router";
const routes = [{
  path: "/reports",
  component: () => import("./ReportsPage.vue")
}];
export const router = createRouter({
  history: createWebHistory(),
  routes
});
~~~

~~~vue
<template>
  <RouterView v-slot="{ Component }">
    <Suspense>
      <component :is="Component" />
      <template #fallback>Загрузка отчётов</template>
    </Suspense>
  </RouterView>
</template>
~~~

Динамический import загружает ReportsPage отдельным chunk при необходимости, уменьшая стартовый bundle. RouterView показывает активный маршрут, Suspense временно отображает fallback, пока асинхронный компонент готов. Полезно также обработать ошибку загрузки chunk. Ленивая маршрутизация — дополнение к темам маршрутов лекций 8–10.
