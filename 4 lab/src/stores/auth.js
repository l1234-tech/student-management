// Назначение: единое состояние пользователя и жизненного цикла сессии.
import { ref } from 'vue'
import { defineStore } from 'pinia'
import * as authApi from '../api/auth'
import {
  clearHttpSession,
  setHttpSession,
  setHttpSessionListener,
} from '../api/http'
import { useStudentsStore } from './students'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const accessToken = ref(null)
  const refreshToken = ref(null)
  const pending = ref(false)
  const error = ref(null)
  const initialized = ref(false)

  let restorePromise = null

  setHttpSessionListener((tokens) => {
    const hadSession = Boolean(
      accessToken.value || refreshToken.value || user.value,
    )

    accessToken.value = tokens.accessToken
    refreshToken.value = tokens.refreshToken

    if (!tokens.accessToken && !tokens.refreshToken) {
      user.value = null

      if (hadSession) {
        useStudentsStore().clear()
      }
    }
  })

  function saveSession(tokens) {
    if (!tokens?.accessToken || !tokens?.refreshToken) {
      throw new Error('Сервер вернул неполную пару токенов')
    }

    setHttpSession(tokens)
  }

  async function loadCurrentUser() {
    user.value = await authApi.me()

    if (!['USER', 'ADMIN'].includes(user.value?.role)) {
      throw new Error('Сервер вернул неизвестную роль')
    }

    return user.value
  }

  async function login(credentials) {
    pending.value = true
    error.value = null

    try {
      saveSession(await authApi.login(credentials))
      await loadCurrentUser()
      initialized.value = true
      return user.value
    } catch (cause) {
      clearHttpSession()
      error.value = cause
      throw cause
    } finally {
      pending.value = false
    }
  }

  async function register(data) {
    pending.value = true
    error.value = null

    try {
      saveSession(await authApi.register(data))
      await loadCurrentUser()
      initialized.value = true
      return user.value
    } catch (cause) {
      clearHttpSession()
      error.value = cause
      throw cause
    } finally {
      pending.value = false
    }
  }

  async function restoreSession() {
    if (initialized.value) {
      return Boolean(user.value)
    }

    if (restorePromise) {
      return restorePromise
    }

    restorePromise = (async () => {
      pending.value = true
      error.value = null

      try {
        if (
          !sessionStorage.getItem('accessToken') &&
          !sessionStorage.getItem('refreshToken')
        ) {
          return false
        }

        await loadCurrentUser()
        return true
      } catch (cause) {
        error.value = cause

        if (cause.status === 401) {
          clearHttpSession()
        }

        return false
      } finally {
        initialized.value = true
        pending.value = false
        restorePromise = null
      }
    })()

    return restorePromise
  }

  async function logout() {
    pending.value = true
    error.value = null

    const currentRefreshToken =
      refreshToken.value || sessionStorage.getItem('refreshToken')
    let revokeError = null

    try {
      if (currentRefreshToken) {
        await authApi.logout(currentRefreshToken)
      }
    } catch (cause) {
      revokeError = cause
      error.value = cause
    } finally {
      clearHttpSession()
      user.value = null
      initialized.value = true
      useStudentsStore().clear()
      pending.value = false
    }

    if (revokeError) {
      throw revokeError
    }

    return true
  }

  return {
    user,
    accessToken,
    refreshToken,
    pending,
    error,
    initialized,
    login,
    register,
    restoreSession,
    logout,
  }
})
