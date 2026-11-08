// Назначение: маршруты SPA и клиентская проверка сессии и ролей.
import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import LoginPage from '../pages/LoginPage.vue'
import RegisterPage from '../pages/RegisterPage.vue'
import ProfilePage from '../pages/ProfilePage.vue'
import StudentsPage from '../pages/StudentsPage.vue'
import StudentPage from '../pages/StudentPage.vue'
import StudentCreatePage from '../pages/StudentCreatePage.vue'
import StudentEditPage from '../pages/StudentEditPage.vue'
import NotFoundPage from '../pages/NotFoundPage.vue'
import ForbiddenPage from '../pages/ForbiddenPage.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/students' },
    { path: '/login', component: LoginPage, meta: { guestOnly: true } },
    { path: '/register', component: RegisterPage, meta: { guestOnly: true } },
    { path: '/forbidden', component: ForbiddenPage },
    { path: '/profile', component: ProfilePage, meta: { requiresAuth: true } },
    {
      path: '/students',
      component: StudentsPage,
      meta: { requiresAuth: true, roles: ['USER', 'ADMIN'] },
    },
    {
      path: '/students/new',
      component: StudentCreatePage,
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    {
      path: '/students/:isu',
      component: StudentPage,
      props: true,
      meta: { requiresAuth: true, roles: ['USER', 'ADMIN'] },
    },
    {
      path: '/students/:isu/edit',
      component: StudentEditPage,
      props: true,
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    { path: '/:pathMatch(.*)*', component: NotFoundPage },
  ],
})

router.beforeEach(async (to) => {
  const auth = useAuthStore()

  if (!auth.initialized) {
    await auth.restoreSession()
  }

  if (to.meta.guestOnly && auth.user) {
    return '/students'
  }

  if (to.meta.requiresAuth && !auth.user) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }

  if (to.meta.roles?.length && !to.meta.roles.includes(auth.user?.role)) {
    return '/forbidden'
  }

  return true
})

export default router
