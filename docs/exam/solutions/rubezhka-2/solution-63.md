# 63. Напишите компонент списка студентов с сортировкой по ФИО и корректными key по isuId. Добавьте кнопку, меняющую направление сортировки; при перестановке строк идентичность компонентов должна сохраняться.

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

~~~vue
<script setup>
import { computed, ref } from "vue";
const ascending = ref(true);
const students = ref([
  { isuId: 18, fullName: "Иванов Иван" },
  { isuId: 7, fullName: "Петров Пётр" },
  { isuId: 25, fullName: "Сидорова Анна" }
]);

const sortedStudents = computed(() =>
  [...students.value].sort((a, b) => {
    const order = a.fullName.localeCompare(b.fullName, "ru");
    return ascending.value ? order : -order;
  })
);
function toggleSort() {
  ascending.value = !ascending.value;
}
</script>
<template>
  <button @click="toggleSort">Поменять направление</button>
  <li v-for="student in sortedStudents" :key="student.isuId">
    {{ student.fullName }}
  </li>
</template>
~~~

ascending хранит направление; computed sortedStudents копирует массив перед sort, потому что sort мутирует массив. localeCompare сравнивает ФИО с русскими правилами. toggleSort меняет boolean. key равен стабильному isuId, поэтому после перестановки Vue сопоставляет DOM и состояние строк с тем же студентом, а не с индексом. Это reconciliation/key из лекции 9.
