<script setup>
// Таблица студентов с переходом в карточку и действиями администратора.
import { RouterLink } from 'vue-router'

defineProps({
  students: {
    type: Array,
    default: () => [],
  },
  isAdmin: Boolean,
})

defineEmits(['remove', 'sort'])
</script>

<template>
  <div class="table-scroll">
    <table>
      <thead>
        <tr>
          <th><button @click="$emit('sort', 'ISU')">ИСУ</button></th>
          <th><button @click="$emit('sort', 'name')">ФИО</button></th>
          <th>Группа</th>
          <th>Общежитие</th>
          <th>Комната</th>
          <th>Дата</th>
          <th>Иностранец</th>
          <th>Заметка</th>
          <th>Действия</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="student in students" :key="student.ISU">
          <td>{{ student.ISU }}</td>
          <td>{{ student.name }}</td>
          <td>{{ student.group }}</td>
          <td>{{ student.dormitory }}</td>
          <td>{{ student.room }}</td>
          <td>{{ student.checkin_date }}</td>
          <td>{{ student.is_foreign ? 'Да' : 'Нет' }}</td>
          <td>{{ student.note }}</td>
          <td>
            <RouterLink :to="`/students/${student.ISU}`">Открыть</RouterLink>
            <template v-if="isAdmin">
              ·
              <RouterLink :to="`/students/${student.ISU}/edit`">Изменить</RouterLink>
              <button @click="$emit('remove', student.ISU)">Удалить</button>
            </template>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
