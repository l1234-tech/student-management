<!-- Назначение: фильтры полей студента и сортировка через v-model:filters. -->
<script setup>
import { computed } from 'vue'

const props = defineProps({
  filters: {
    type: Object,
    default: () => ({}),
  },
})

const emit = defineEmits(['update:filters', 'apply', 'reset'])
const model = computed({
  get: () => props.filters,
  set: (value) => emit('update:filters', value),
})

function set(key, value) {
  model.value = { ...model.value, [key]: value }
}
</script>

<template>
  <fieldset>
    <legend>Фильтры</legend>
    <div class="filter-grid">
      <label>
        ИСУ
        <input
          type="number"
          :value="filters.ISU"
          @input="set('ISU', $event.target.value === '' ? '' : Number($event.target.value))"
        />
      </label>
      <label>
        Группа
        <input :value="filters.group" @input="set('group', $event.target.value)" />
      </label>
      <label>
        Общежитие
        <input
          type="number"
          :value="filters.dormitory"
          @input="set('dormitory', $event.target.value === '' ? '' : Number($event.target.value))"
        />
      </label>
      <label>
        Комната
        <input
          type="number"
          :value="filters.room"
          @input="set('room', $event.target.value === '' ? '' : Number($event.target.value))"
        />
      </label>
      <label>
        Дата заселения
        <input
          type="date"
          :value="filters.checkin_date"
          @input="set('checkin_date', $event.target.value)"
        />
      </label>
      <label>
        Иностранец
        <select
          :value="filters.is_foreign == null ? '' : String(filters.is_foreign)"
          @change="
            set(
              'is_foreign',
              $event.target.value === '' ? null : $event.target.value === 'true',
            )
          "
        >
          <option value="">Любой</option>
          <option value="true">Да</option>
          <option value="false">Нет</option>
        </select>
      </label>
      <label>
        Заметка
        <input :value="filters.note" @input="set('note', $event.target.value)" />
      </label>
      <label>
        Сортировка
        <select :value="filters.sort" @change="set('sort', $event.target.value)">
          <option
            v-for="field in ['ISU', 'name', 'group', 'dormitory', 'room', 'checkinDate', 'isForeign', 'note']"
            :key="field"
            :value="`${field},asc`"
          >
            {{ field }} ↑
          </option>
          <option
            v-for="field in ['ISU', 'name', 'group', 'dormitory', 'room', 'checkinDate', 'isForeign', 'note']"
            :key="field + '-desc'"
            :value="`${field},desc`"
          >
            {{ field }} ↓
          </option>
        </select>
      </label>
    </div>
    <button type="button" @click="$emit('apply')">Применить</button>
    <button type="button" @click="$emit('reset')">Сбросить</button>
  </fieldset>
</template>
