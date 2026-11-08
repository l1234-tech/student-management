<!-- Назначение: отображение профиля и завершение текущей сессии. -->
<script setup>
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useAuthStore } from '../stores/auth'
import LoadingIndicator from '../components/LoadingIndicator.vue'
import ErrorMessage from '../components/ErrorMessage.vue'

const auth = useAuthStore()
const router = useRouter()
const { user, pending, error } = storeToRefs(auth)

async function logout() {
  try {
    await auth.logout()
    await router.replace('/login')
  } catch {
    await router.replace({ path: '/login', query: { reason: 'logout-network' } })
  }
}
</script>

<template>
  <section>
    <h2>Профиль</h2>
    <LoadingIndicator :active="pending" />
    <ErrorMessage :error="error" />
    <dl v-if="user">
      <dt>Имя пользователя</dt>
      <dd>{{ user.username }}</dd>
      <dt>Роль</dt>
      <dd>{{ user.role }}</dd>
    </dl>
    <p>Email и редактирование профиля не предоставляются текущим API.</p>
    <button type="button" :disabled="pending" @click="logout">Выйти</button>
  </section>
</template>
