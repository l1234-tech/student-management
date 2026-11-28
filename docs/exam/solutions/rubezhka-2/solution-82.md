# 82. Напишите компонент на выбранном frontend-фреймворке, который показывает текущую тему («светлая» или «тёмная») и переключает её по кнопке. Сохраняйте выбор в localStorage и восстанавливайте при повторном открытии страницы.

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

~~~vue
<script setup>
import { onMounted, ref, watch } from "vue";
const theme = ref("light");

onMounted(() => {
  theme.value = localStorage.getItem("theme") ?? "light";
});
watch(theme, value => localStorage.setItem("theme", value));

function toggleTheme() {
  theme.value = theme.value === "light" ? "dark" : "light";
}
</script>
<template>
  <main :data-theme="theme">
    <p>Тема: {{ theme === "light" ? "светлая" : "тёмная" }}</p>
    <button @click="toggleTheme">Переключить</button>
  </main>
</template>
~~~

theme — реактивное значение. onMounted читает сохранённый выбор после загрузки на клиенте; localStorage отсутствует в SSR. ?? задаёт light при отсутствии сохранения. watch сохраняет изменение. toggleTheme переключает значения; data-theme позволяет CSS применить палитру. Чтение после монтирования также помогает избежать hydration mismatch (лекция 10).
