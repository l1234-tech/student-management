# 69. Напишите компонент StudentList, который принимает loading, error и students и отображает соответственно сообщение «Загрузка», ошибку, «Студенты не найдены» или список карточек студентов. Реализуйте условия в коде выбранного фреймворка.

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

~~~vue
<script setup>
defineProps({
  loading: Boolean,
  error: String,
  students: { type: Array, default: () => [] }
});
</script>
<template>
  <p v-if="loading">Загрузка</p>
  <p v-else-if="error" role="alert">{{ error }}</p>
  <p v-else-if="students.length === 0">Студенты не найдены</p>
  <StudentCard
    v-else
    v-for="student in students"
    :key="student.isuId"
    :student="student"
  />
</template>
~~~

Props описывают входной контракт: loading — запрос выполняется, error — текст ошибки, students — массив. Цепочка условий отображает только одно состояние: загрузка, ошибка, пустой успешный ответ или карточки. Пустой массив — не ошибка. role=alert помогает доступности. key использует стабильный isuId. Состояния загрузки/ошибки разобраны в лекции 10.
