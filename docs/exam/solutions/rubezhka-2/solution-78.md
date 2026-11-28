# 78. Напишите код компонента, подписывающегося на students$ при монтировании и обновляющего список студентов при новых значениях. При уничтожении компонента выполните unsubscribe; используйте lifecycle/hook выбранного фреймворка.

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

~~~vue
<script setup>
import { onMounted, onUnmounted, ref } from "vue";
const students = ref([]);
let subscription;

onMounted(() => {
  subscription = students$.subscribe({
    next: value => { students.value = value; },
    error: error => console.error(error)
  });
});
onUnmounted(() => subscription?.unsubscribe());
</script>
<template>
  <li v-for="student in students" :key="student.isuId">
    {{ student.name }}
  </li>
</template>
~~~

students — ref, связанный с шаблоном. students$ — Observable сервиса. subscribe запускает/подключает поток; next callback получает значения и обновляет массив. subscription хранит handle для отписки. onUnmounted прекращает получение событий и освобождает ресурсы; отменит ли это underlying запрос, зависит от источника. Это RxJS subscription плюс lifecycle из лекции 9.
