<script setup>
// Создаёт студента и открывает карточку после успешного ответа backend.
import { useRouter } from 'vue-router'
import { useStudentsStore } from '../stores/students'
import StudentForm from '../components/StudentForm.vue'

const router = useRouter()
const store = useStudentsStore()

async function submit(data) {
  try {
    const student = await store.create(data)
    router.push('/students/' + student.ISU)
  } catch {
  }
}
</script>

<template>
  <section>
    <h2>Новый студент</h2>
    <StudentForm
      :pending="store.pending"
      :errors="store.error?.details"
      @submit="submit"
      @cancel="router.push('/students')"
    />
    <p v-if="store.error" role="alert">{{ store.error.message }}</p>
  </section>
</template>
