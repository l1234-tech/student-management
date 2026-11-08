// Назначение: сборка Vue и dev proxy; вход .env, выход dist/ и маршрутизация dev-запросов.
// TODO: При смене backend изменить API_PROXY_TARGET, сохранить /api без rewrite.
// Согласовать с src/api/http.js и nginx.conf; proxy устраняет потребность в CORS при локальной разработке.
// API /api/auth/*, /api/user/* (USER/ADMIN), /api/admin/* (ADMIN); proxy не проверяет JWT/роли.
// TODO: Проверить npm run build, /students/100000 после reload; после реализации API проверить Network.
// preview показывает сборку, но API proxy ниже настроен только для dev; для API в production нужен Nginx.
import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')

  return {
    plugins: [vue()],
    server: {
      port: 5173,
      proxy: {
        '/api': {
          target: env.API_PROXY_TARGET || 'http://localhost:8080',
          changeOrigin: true,
        },
      },
    },
  }
})
