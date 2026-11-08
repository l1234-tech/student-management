<script setup>
// Загружает запись студента и сохраняет её изменения.
import { onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useStudentsStore } from '../stores/students'
import StudentForm from '../components/StudentForm.vue'

const props = defineProps({
  isu: {
    type: String,
    required: true,
  },
})

const router = useRouter()
const store = useStudentsStore()

async function load() {
  try {
    await store.loadOne(props.isu)
  } catch {
  }
}

async function submit(data) {
  try {
    const student = await store.update(props.isu, data)
    router.push('/students/' + student.ISU)
  } catch {
  }
}

watch(() => props.isu, load)
onMounted(load)
</script>

<template>
  <section>
    <h2>Редактирование</h2>
    <p v-if="store.error" role="alert">{{ store.error.message }}</p>
    <StudentForm
      v-if="store.selected"
      mode="edit"
      :initial-student="store.selected"
      :pending="store.pending"
      :errors="store.error?.details"
      @submit="submit"
      @cancel="router.push('/students/' + props.isu)"
    />
  </section>
</template>
