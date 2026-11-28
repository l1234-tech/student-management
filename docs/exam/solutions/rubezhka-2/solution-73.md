# 73. Напишите код родительской страницы с общим фильтром группы для StudentFilter и StudentList. Состояние фильтра храните в родителе, состояние открытой модалки — локально в StudentList, а текущего пользователя получите из глобального auth-store.

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

~~~vue
<!-- StudentPage.vue -->
<script setup>
import { computed, ref } from "vue";
import { useAuthStore } from "@/stores/auth";
const auth = useAuthStore();
const groupFilter = ref("");
const students = ref([
  { isuId: 101, name: "Анна", group: "M3201" },
  { isuId: 102, name: "Борис", group: "M3202" }
]);
const filtered = computed(() =>
  groupFilter.value
    ? students.value.filter(s => s.group === groupFilter.value)
    : students.value
);
</script>
<template>
  <p>{{ auth.user?.name ?? "Гость" }}</p>
  <StudentFilter v-model:group="groupFilter" />
  <StudentList :students="filtered" />
</template>
~~~

~~~vue
<!-- StudentList.vue -->
<script setup>
import { ref } from "vue";
defineProps({ students: Array });
const openedId = ref(null);
</script>
<template>
  <StudentCard
    v-for="student in students"
    :key="student.isuId"
    :student="student"
    @edit="openedId = $event"
  />
  <StudentModal v-if="openedId !== null" :isu-id="openedId"
    @close="openedId = null" />
</template>
~~~

groupFilter принадлежит родителю, так как фильтрует и управляет двумя дочерними блоками. filtered — computed список. auth.user берётся из глобального store. openedId — локальное состояние StudentList для выбора/открытия модалки; событие edit передаёт id. Состояние не помещается в глобальный store без необходимости. Это разделение local/shared state из лекции 9.
