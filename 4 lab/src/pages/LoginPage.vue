<!-- Назначение: форма входа и переход на запрошенную внутреннюю страницу. -->
<script setup>
import { reactive } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useAuthStore } from '../stores/auth'
import ErrorMessage from '../components/ErrorMessage.vue'
import LoadingIndicator from '../components/LoadingIndicator.vue'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()
const { pending, error } = storeToRefs(auth)
const form = reactive({ username: '', password: '' })

function destination() {
  const target =
    typeof route.query.redirect === 'string' ? route.query.redirect : '/students'

  return target.startsWith('/') &&
    !target.startsWith('//') &&
    !target.startsWith('/login') &&
    !target.startsWith('/register')
    ? target
    : '/students'
}

async function submit() {
  if (pending.value || !form.username.trim() || !form.password) {
    return
  }

  try {
    await auth.login({
      username: form.username.trim(),
      password: form.password,
    })
    await router.replace(destination())
  } catch {
  }
}
</script>

<template>
  <section>
    <h2>Вход</h2>
    <form @submit.prevent="submit">
      <label>
        Имя пользователя или email
        <input
          v-model.trim="form.username"
          autocomplete="username"
          required
        />
      </label>
      <label>
        Пароль
        <input
          v-model="form.password"
          type="password"
          autocomplete="current-password"
          required
        />
      </label>
      <button
        type="submit"
        :disabled="pending || !form.username.trim() || !form.password"
      >
        Войти
      </button>
    </form>
    <LoadingIndicator :active="pending" />
    <ErrorMessage :error="error" />
    <p>Нет аккаунта? <RouterLink to="/register">Зарегистрироваться</RouterLink></p>
  </section>
</template>
