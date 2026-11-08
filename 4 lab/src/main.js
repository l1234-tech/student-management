// Назначение: инициализация Vue, Pinia и сессии до первой маршрутизации.
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { useAuthStore } from './stores/auth'
import './styles/main.css'

async function start() {
  const app = createApp(App)
  const pinia = createPinia()

  app.use(pinia)
  await useAuthStore(pinia).restoreSession()
  app.use(router)
  await router.isReady()
  app.mount('#app')
}

start()
