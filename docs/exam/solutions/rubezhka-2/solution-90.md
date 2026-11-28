# 90. Напишите страницу /students на выбранном метафреймворке: укажите файл маршрута, задайте массив из двух студентов с полями id, name и group, выведите ФИО, группу и ссылку /students/{id} для каждого студента.

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

~~~vue
<!-- app/pages/students/index.vue -->
<script setup>
const students = [
  { id: 101, name: "Анна Иванова", group: "M3201" },
  { id: 102, name: "Борис Петров", group: "M3202" }
];
</script>
<template>
  <main>
    <h1>Студенты</h1>
    <ul>
      <li v-for="student in students" :key="student.id">
        <NuxtLink :to="'/students/' + student.id">
          {{ student.name }} — {{ student.group }}
        </NuxtLink>
      </li>
    </ul>
  </main>
</template>
~~~

Файл app/pages/students/index.vue автоматически соответствует /students. students — требуемые две записи; v-for формирует элемент списка, key использует уникальный id. NuxtLink создаёт внутреннюю ссылку, например /students/101, соответствующую динамической странице app/pages/students/[id].vue из вопроса 87. Это файловая маршрутизация и клиентская навигация из лекции 10.
