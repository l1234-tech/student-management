<!-- Назначение: загрузка карточки студента и административные действия. -->
<script setup>
import { computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useAuthStore } from '../stores/auth'
import { useStudentsStore } from '../stores/students'
import LoadingIndicator from '../components/LoadingIndicator.vue'
import ErrorMessage from '../components/ErrorMessage.vue'

const props = defineProps({
  isu: {
    type: String,
    required: true,
  },
})

const router = useRouter()
const store = useStudentsStore()
const auth = useAuthStore()
const { selected, pending, error } = storeToRefs(store)
const isAdmin = computed(() => auth.user?.role === 'ADMIN')

let controller

async function load() {
  controller?.abort()
  controller = new AbortController()

  try {
    await store.loadOne(props.isu, controller.signal)
  } catch {
  }
}

async function remove() {
  if (!isAdmin.value || !window.confirm('Удалить студента?')) {
    return
  }

  try {
    await store.remove(props.isu)
    await router.push('/students')
  } catch {
  }
}

watch(() => props.isu, load)
onMounted(load)
onUnmounted(() => controller?.abort())
</script>

<template>
  <section>
    <h2>Карточка студента</h2>
    <LoadingIndicator :active="pending" />
    <ErrorMessage :error="error" />
    <dl v-if="selected">
      <dt>ИСУ</dt>
      <dd>{{ selected.ISU }}</dd>
      <dt>ФИО</dt>
      <dd>{{ selected.name }}</dd>
      <dt>Группа</dt>
      <dd>{{ selected.group }}</dd>
      <dt>Общежитие</dt>
      <dd>{{ selected.dormitory }}</dd>
      <dt>Комната</dt>
      <dd>{{ selected.room }}</dd>
      <dt>Дата заселения</dt>
      <dd>{{ selected.checkin_date }}</dd>
      <dt>Иностранец</dt>
      <dd>{{ selected.is_foreign ? 'Да' : 'Нет' }}</dd>
      <dt>Заметка</dt>
      <dd>{{ selected.note }}</dd>
    </dl>
    <RouterLink to="/students">К списку</RouterLink>
    <template v-if="isAdmin && selected">
      <RouterLink :to="`/students/${props.isu}/edit`">Изменить</RouterLink>
      <button type="button" :disabled="pending" @click="remove">Удалить</button>
    </template>
  </section>
</template>
