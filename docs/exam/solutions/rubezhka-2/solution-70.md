# 70. Напишите пример с реактивным count и вычисляемым doubled = count * 2 на выбранном frontend-фреймворке. Добавьте watcher/effect, который записывает doubled в консоль при изменении count, и кнопку увеличения count.

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

~~~vue
<script setup>
import { computed, ref, watch } from "vue";
const count = ref(0);
const doubled = computed(() => count.value * 2);

watch(count, newCount => {
  console.log("doubled =", newCount * 2);
});
function increment() { count.value += 1; }
</script>
<template>
  <p>count={{ count }}, doubled={{ doubled }}</p>
  <button @click="increment">Увеличить</button>
</template>
~~~

count — исходное состояние. doubled — производное computed, автоматически обновляемое при изменении count; отдельно хранить его не надо. watch выполняет побочный эффект при изменении, здесь пишет вычисленное значение в консоль. newCount — новое значение счётчика. increment увеличивает его. В лекции 9 computed предназначен для производных данных, watcher/effect — для побочного действия.
