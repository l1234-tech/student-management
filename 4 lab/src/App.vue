<!-- Назначение: общий каркас приложения с навигацией по состоянию сессии. -->
<script setup>
import { computed } from 'vue'
import { RouterLink, RouterView, useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useAuthStore } from './stores/auth'

const auth = useAuthStore()
const router = useRouter()
const { user, pending } = storeToRefs(auth)
const isAdmin = computed(() => user.value?.role === 'ADMIN')

async function logout() {
  try {
    await auth.logout()
  } catch {
  } finally {
    router.push('/login')
  }
}
</script>

<template>
  <header>
    <h1>Управление студентами</h1>
    <nav aria-label="Основная навигация">
      <template v-if="user">
        <RouterLink to="/students">Студенты</RouterLink>
        <RouterLink v-if="isAdmin" to="/students/new">Создание</RouterLink>
        <RouterLink to="/profile">Профиль ({{ user.username }})</RouterLink>
        <button type="button" :disabled="pending" @click="logout">Выйти</button>
      </template>
      <template v-else>
        <RouterLink to="/login">Вход</RouterLink>
        <RouterLink to="/register">Регистрация</RouterLink>
      </template>
    </nav>
  </header>
  <main>
    <RouterView />
  </main>
</template>
