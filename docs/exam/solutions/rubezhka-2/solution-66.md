# 66. Напишите родительский и дочерний компоненты: дочерний получает имя студента через props/input, принимает новое имя в поле ввода и сообщает о сохранении родителю событием/callback; родитель обновляет своё состояние.

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

~~~vue
<!-- Child.vue -->
<script setup>
import { ref, watch } from "vue";
const props = defineProps({ name: { type: String, required: true } });
const emit = defineEmits(["save"]);
const draftName = ref(props.name);

watch(() => props.name, value => { draftName.value = value; });
function save() { emit("save", draftName.value); }
</script>
<template>
  <input v-model="draftName" />
  <button @click="save">Сохранить</button>
</template>
~~~

~~~vue
<!-- Parent.vue -->
<script setup>
import { ref } from "vue";
const studentName = ref("Анна Иванова");
function updateName(value) { studentName.value = value; }
</script>
<template>
  <Child :name="studentName" @save="updateName" />
  <p>{{ studentName }}</p>
</template>
~~~

name — входной prop, который ребёнок напрямую не меняет. draftName — локальный черновик. watcher синхронизирует черновик при новом prop. save отправляет новое значение событием; родитель updateName обновляет своё состояние, и Vue обновляет экран. Это передача данных сверху вниз и событие вверх.
