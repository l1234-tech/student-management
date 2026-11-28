# 61. Напишите код страницы StudentPage, которая использует компоненты StudentFilter и StudentList. Фильтр по группе хранится в StudentPage и передаётся дочерним компонентам; список обновляется при изменении фильтра. Используйте любой изученный frontend-фреймворк.

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

~~~vue
<script setup>
import { computed, ref } from "vue";
import StudentFilter from "./StudentFilter.vue";
import StudentList from "./StudentList.vue";

const students = ref([
  { isuId: 101, name: "Анна Иванова", group: "M3201" },
  { isuId: 102, name: "Борис Петров", group: "M3202" }
]);
const selectedGroup = ref("");

const visibleStudents = computed(() =>
  selectedGroup.value
    ? students.value.filter(s => s.group === selectedGroup.value)
    : students.value
);
</script>
<template>
  <StudentFilter v-model:group="selectedGroup" />
  <StudentList :students="visibleStudents" />
</template>
~~~

students — исходный реактивный массив; selectedGroup — общее состояние фильтра. Пустая строка означает все группы. visibleStudents — computed-производное значение, которое автоматически пересчитывается при изменении входных данных. Родитель передаёт отфильтрованный список в StudentList, а StudentFilter сообщает новое значение через update:group. Это props вниз/events вверх из лекции 9.
