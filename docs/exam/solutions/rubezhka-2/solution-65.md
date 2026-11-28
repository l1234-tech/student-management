# 65. Напишите код разбиения StudentPage на StudentToolbar, StudentList и StudentCard: StudentToolbar вызывает callback смены фильтра, StudentList получает массив студентов, StudentCard получает одного студента и вызывает onEdit(isuId). Покажите использование компонентов в StudentPage.

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

~~~vue
<!-- StudentPage.vue -->
<script setup>
import { ref } from "vue";
const students = ref([]);
const groupFilter = ref("");

function setGroup(group) {
  groupFilter.value = group;
}
function editStudent(isuId) {
  console.log("Редактировать", isuId);
}
</script>
<template>
  <StudentToolbar :group="groupFilter" @change-group="setGroup" />
  <StudentList :students="students" @edit="editStudent" />
</template>
~~~

~~~vue
<!-- StudentToolbar.vue -->
<script setup>
defineProps({ group: String });
defineEmits(["change-group"]);
</script>
<template>
  <select :value="group" @change="$emit('change-group', $event.target.value)">
    <option value="">Все группы</option>
    <option value="M3201">M3201</option>
    <option value="M3202">M3202</option>
  </select>
</template>
~~~

~~~vue
<!-- StudentList.vue -->
<script setup>
defineProps({ students: { type: Array, default: () => [] } });
defineEmits(["edit"]);
</script>
<template>
  <StudentCard
    v-for="student in students"
    :key="student.isuId"
    :student="student"
    @edit="$emit('edit', $event)"
  />
</template>
~~~

~~~vue
<!-- StudentCard.vue -->
<script setup>
defineProps({ student: { type: Object, required: true } });
defineEmits(["edit"]);
</script>
<template>
  <article>
    <h2>{{ student.name }}</h2>
    <p>{{ student.group }}</p>
    <button @click="$emit('edit', student.isuId)">Редактировать</button>
  </article>
</template>
~~~

StudentPage владеет общими данными и groupFilter. StudentToolbar получает группу через prop, а событие change-group несёт выбранное значение; setGroup обновляет состояние родителя. StudentList получает массив, создаёт карточку для каждой записи и использует isuId как стабильный key. StudentCard показывает одного студента, а событие edit передаёт вверх его isuId; List пробрасывает это событие родителю, где editStudent открывает форму редактирования. В цепочке данные идут вниз через props, события — вверх. Это контракт компонентов из лекции 9.
