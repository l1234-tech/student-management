# 87. Напишите код динамического маршрута /students/[id] в выбранном метафреймворке. Получите id из параметров, загрузите данные студента на сервере, выведите ФИО и группу; при отсутствии студента верните страницу 404.

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

~~~vue
<!-- app/pages/students/[id].vue -->
<script setup>
const route = useRoute();
const id = computed(() => String(route.params.id));
const { data: student, error } = await useAsyncData(
  () => "student:" + id.value,
  () => $fetch("/api/students/" + encodeURIComponent(id.value))
);
if (error.value?.statusCode === 404) {
  throw createError({ statusCode: 404, statusMessage: "Студент не найден" });
}
const fullName = computed(() => student.value?.fullName ?? "");
const group = computed(() => student.value?.group ?? "");
</script>
<template>
  <main v-if="student">
    <h1>{{ fullName }}</h1>
    <p>Группа: {{ group }}</p>
  </main>
</template>
~~~

[ id ] в имени файла — динамический параметр; useRoute читает его, computed приводит к строке. Ключ useAsyncData зависит от id, чтобы данные разных студентов не смешивались. Загрузчик обращается к API на сервере при SSR и передаёт результат браузеру для hydration. Ошибка 404 превращается в корректный HTTP 404 через createError. fullName/group — вычисляемые поля. Другие ошибки нельзя выдавать за отсутствие записи. Источник: лекция 10, [Nuxt useAsyncData](https://nuxt.com/docs/api/composables/use-async-data).
