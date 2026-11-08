<!-- Назначение: регистрация пользователя с ролью USER и автоматический вход. -->
<script setup>
import { reactive, ref } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useAuthStore } from '../stores/auth'
import ErrorMessage from '../components/ErrorMessage.vue'
import LoadingIndicator from '../components/LoadingIndicator.vue'

const auth = useAuthStore()
const router = useRouter()
const { pending, error } = storeToRefs(auth)
const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
})
const validation = ref('')

async function submit() {
  validation.value = ''

  if (pending.value) {
    return
  }

  if (!/^[A-Za-z0-9_]{3,50}$/.test(form.username)) {
    validation.value =
      'Имя пользователя: 3–50 символов, латинские буквы, цифры и _.'
    return
  }

  if (!/^\S+@\S+\.\S+$/.test(form.email) || form.email.length > 254) {
    validation.value = 'Введите корректный email (до 254 символов).'
    return
  }

  if (
    form.password.length < 8 ||
    form.password.length > 72 ||
    new TextEncoder().encode(form.password).length > 72
  ) {
    validation.value = 'Пароль: 8–72 символа и не более 72 байт UTF-8.'
    return
  }

  if (form.password !== form.confirmPassword) {
    validation.value = 'Пароли не совпадают.'
    return
  }

  try {
    await auth.register({
      username: form.username,
      email: form.email.trim(),
      password: form.password,
    })
    await router.replace('/students')
  } catch {
  }
}
</script>

<template>
  <section>
    <h2>Регистрация</h2>
    <form @submit.prevent="submit">
      <label>
        Имя пользователя
        <input
          v-model.trim="form.username"
          autocomplete="username"
          minlength="3"
          maxlength="50"
          required
        />
      </label>
      <label>
        Email
        <input
          v-model.trim="form.email"
          type="email"
          autocomplete="email"
          maxlength="254"
          required
        />
      </label>
      <label>
        Пароль
        <input
          v-model="form.password"
          type="password"
          autocomplete="new-password"
          minlength="8"
          maxlength="72"
          required
        />
      </label>
      <label>
        Повторите пароль
        <input
          v-model="form.confirmPassword"
          type="password"
          autocomplete="new-password"
          required
        />
      </label>
      <p v-if="validation" role="alert">{{ validation }}</p>
      <button type="submit" :disabled="pending">Зарегистрироваться</button>
    </form>
    <LoadingIndicator :active="pending" />
    <ErrorMessage :error="error" />
    <p>Уже есть аккаунт? <RouterLink to="/login">Войти</RouterLink></p>
    <p>После регистрации назначается роль USER.</p>
  </section>
</template>
