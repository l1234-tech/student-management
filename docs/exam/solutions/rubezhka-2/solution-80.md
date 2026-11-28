# 80. Напишите компонент с кнопкой «Загрузить студентов». При нажатии он выполняет GET /api/students и показывает loading, success или error; пока запрос выполняется, повторное нажатие заблокировано, после ошибки можно повторить попытку.

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

~~~vue
<script setup>
import { ref } from "vue";
const status = ref("idle");
const students = ref([]);
const errorMessage = ref("");

async function loadStudents() {
  if (status.value === "loading") return;
  status.value = "loading";
  errorMessage.value = "";
  try {
    const response = await fetch("/api/students");
    if (!response.ok) throw new Error("HTTP " + response.status);
    students.value = await response.json();
    status.value = "success";
  } catch {
    errorMessage.value = "Не удалось загрузить студентов";
    status.value = "error";
  }
}
</script>
<template>
  <button :disabled="status === 'loading'" @click="loadStudents">
    {{ status === "loading" ? "Загрузка..." : "Загрузить студентов" }}
  </button>
  <p v-if="status === 'success'">Получено: {{ students.length }}</p>
  <p v-if="status === 'error'" role="alert">{{ errorMessage }}</p>
</template>
~~~

status описывает состояния idle/loading/success/error. Проверка в начале и disabled предотвращают повторный клик во время запроса. students хранит успешный JSON, errorMessage — сообщение. При ошибке кнопка снова доступна и запрос можно повторить. Успешный пустой список остаётся success, а не error.
