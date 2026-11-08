<script setup>
// Общая форма создания и редактирования студента с клиентской валидацией.
import { reactive, watch } from 'vue'

const props = defineProps({
  initialStudent: { type: Object, default: null },
  mode: { type: String, default: 'create' },
  pending: Boolean,
  errors: { type: Object, default: () => ({}) },
})

const emit = defineEmits(['submit', 'cancel'])
const form = reactive({})

function fill(student = {}) {
  Object.assign(form, {
    ISU: student.ISU ?? '',
    name: student.name ?? '',
    group: student.group ?? '',
    dormitory: student.dormitory ?? '',
    room: student.room ?? '',
    checkin_date: student.checkin_date ?? student.checkinDate ?? '',
    is_foreign: student.is_foreign ?? student.isForeign ?? false,
    note: student.note ?? '',
    validation: {},
  })
}

watch(() => props.initialStudent, fill, { immediate: true })

function submit() {
  const data = {
    ...form,
    ISU: Number(form.ISU),
    dormitory: Number(form.dormitory),
    room: Number(form.room),
    name: String(form.name).trim(),
    group: String(form.group).trim(),
    note: String(form.note).trim(),
  }
  const errors = {}

  if (!Number.isInteger(data.ISU) || data.ISU < 100000 || data.ISU > 999999) {
    errors.ISU = 'ИСУ 100000–999999'
  }

  if (
    !/^[A-Za-zА-Яа-яЁё]{2,}(?:-[A-Za-zА-Яа-яЁё]{2,})*(?: [A-Za-zА-Яа-яЁё]{2,}(?:-[A-Za-zА-Яа-яЁё]{2,})*){1,2}$/.test(
      data.name,
    ) ||
    data.name.length > 60
  ) {
    errors.name = 'Некорректное ФИО'
  }

  if (!/^[A-Za-zА-Яа-яЁё][0-9]{4,5}$/.test(data.group)) {
    errors.group = 'Формат группы: буква и 4–5 цифр'
  }

  if (
    !Number.isInteger(data.dormitory) ||
    data.dormitory < 1 ||
    data.dormitory > 20
  ) {
    errors.dormitory = 'Общежитие 1–20'
  }

  if (!Number.isInteger(data.room) || data.room < 1000 || data.room > 9999) {
    errors.room = 'Комната 1000–9999'
  }

  const date = new Date(data.checkin_date)
  if (
    !/^\d{4}-\d{2}-\d{2}$/.test(data.checkin_date) ||
    Number.isNaN(date.valueOf()) ||
    date.getFullYear() < 2016 ||
    date > new Date()
  ) {
    errors.checkin_date = 'Дата с 2016 года по сегодня'
  }

  if (data.note.length > 2000) {
    errors.note = 'Не более 2000 символов'
  }

  if (Object.keys(errors).length) {
    form.validation = errors
    return
  }

  delete data.validation
  data.is_foreign = Boolean(form.is_foreign)
  emit('submit', data)
}
</script>

<template>
  <form @submit.prevent="submit">
    <label>
      ИСУ
      <input v-model="form.ISU" type="number" required :readonly="mode === 'edit'" />
    </label>
    <small>{{ errors.ISU || form.validation?.ISU }}</small>

    <label>
      ФИО
      <input v-model="form.name" maxlength="60" required />
    </label>
    <small>{{ errors.name || form.validation?.name }}</small>

    <label>
      Группа
      <input v-model="form.group" required />
    </label>
    <small>{{ errors.group || form.validation?.group }}</small>

    <label>
      Общежитие
      <input v-model="form.dormitory" type="number" min="1" max="20" required />
    </label>
    <small>{{ errors.dormitory || form.validation?.dormitory }}</small>

    <label>
      Комната
      <input v-model="form.room" type="number" min="1000" max="9999" required />
    </label>
    <small>{{ errors.room || form.validation?.room }}</small>

    <label>
      Дата заселения
      <input v-model="form.checkin_date" type="date" min="2016-01-01" required />
    </label>
    <small>
      {{ errors.checkinDate || errors.checkin_date || form.validation?.checkin_date }}
    </small>

    <label>
      <input v-model="form.is_foreign" type="checkbox" />
      Иностранец
    </label>

    <label>
      Заметка
      <textarea v-model="form.note" maxlength="2000" />
    </label>
    <small>{{ errors.note || form.validation?.note }}</small>

    <button :disabled="pending">Сохранить</button>
    <button type="button" :disabled="pending" @click="$emit('cancel')">
      Отмена
    </button>
  </form>
</template>
