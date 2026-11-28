# 64. Напишите компонент, который при монтировании запускает setInterval для обновления счётчика каждую секунду и регистрирует window.addEventListener("resize", ...). При уничтожении компонента корректно удалите таймер и обработчик.

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

~~~vue
<script setup>
import { onMounted, onUnmounted, ref } from "vue";
const seconds = ref(0);
let timerId;
let resizeHandler;

onMounted(() => {
  timerId = window.setInterval(() => { seconds.value += 1; }, 1000);
  resizeHandler = () => console.log(window.innerWidth);
  window.addEventListener("resize", resizeHandler);
});
onUnmounted(() => {
  window.clearInterval(timerId);
  window.removeEventListener("resize", resizeHandler);
});
</script>
<template><p>Секунд: {{ seconds }}</p></template>
~~~

onMounted выполняется в браузере после вставки компонента в DOM, поэтому window не вызывается во время SSR. timerId сохраняет id интервала; resizeHandler — ссылку на функцию, нужную для удаления. Callback интервала каждую секунду увеличивает seconds. onUnmounted освобождает таймер и обработчик, предотвращая фоновые callbacks и утечку. Это lifecycle hooks из лекции 9.
