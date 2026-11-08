// Назначение: единый HTTP-клиент, обработка ошибок и обновление JWT.
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api'

let refreshing = null
let sessionListener = null

const tokenPair = () => ({
  accessToken: sessionStorage.getItem('accessToken'),
  refreshToken: sessionStorage.getItem('refreshToken'),
})

export function setHttpSessionListener(listener) {
  sessionListener = listener
  listener?.(tokenPair())
}

export function setHttpSession(pair) {
  sessionStorage.setItem('accessToken', pair.accessToken)
  sessionStorage.setItem('refreshToken', pair.refreshToken)
  sessionListener?.(tokenPair())
}

export function clearHttpSession() {
  sessionStorage.removeItem('accessToken')
  sessionStorage.removeItem('refreshToken')
  sessionListener?.({ accessToken: null, refreshToken: null })
}

function createError(status, data, fallback) {
  const result = new Error(
    data?.error?.message || data?.message || fallback || 'Ошибка запроса',
  )
  result.status = status || null
  result.code = data?.error?.code || 'REQUEST_ERROR'
  result.details = data?.error?.details || data?.details || {}
  return result
}

async function refreshSession() {
  if (!refreshing) {
    refreshing = (async () => {
      const refreshToken = tokenPair().refreshToken

      if (!refreshToken) {
        throw createError(401, null, 'Сессия завершена')
      }

      const response = await send(
        '/auth/refresh',
        { method: 'POST', body: { refreshToken }, auth: false },
        false,
      )

      if (!response?.accessToken || !response?.refreshToken) {
        throw createError(401, null, 'Некорректная пара токенов')
      }

      setHttpSession(response)
    })().finally(() => {
      refreshing = null
    })
  }

  return refreshing
}

async function send(path, options = {}, allowRefresh = true) {
  const { method = 'GET', body, signal, auth = true } = options
  const normalizedPath = path.replace(/^\//, '')
  const headers = new Headers(options.headers || {})
  const tokens = tokenPair()

  if (body !== undefined) {
    headers.set('Content-Type', 'application/json')
  }

  if (auth && tokens.accessToken) {
    headers.set('Authorization', `Bearer ${tokens.accessToken}`)
  }

  let response

  try {
    response = await fetch(
      `${API_BASE_URL.replace(/\/$/, '')}/${normalizedPath}`,
      {
        method,
        headers,
        body: body === undefined ? undefined : JSON.stringify(body),
        signal,
      },
    )
  } catch (cause) {
    if (cause.name === 'AbortError') {
      throw cause
    }
    throw createError(null, null, 'Сервер недоступен')
  }

  const authEndpoint = [
    'auth/login',
    'auth/register',
    'auth/refresh',
    'auth/logout',
  ].includes(normalizedPath)

  if (
    response.status === 401 &&
    auth &&
    allowRefresh &&
    !authEndpoint &&
    !signal?.aborted
  ) {
    try {
      await refreshSession()
      return send(path, options, false)
    } catch (cause) {
      if (cause.status === 401) {
        clearHttpSession()
      }
      throw cause
    }
  }

  if (response.status === 204) {
    return undefined
  }

  const raw = await response.text()
  let data = null

  try {
    data = raw ? JSON.parse(raw) : null
  } catch {
    data = { message: raw }
  }

  if (!response.ok) {
    throw createError(response.status, data)
  }

  return data
}

export const request = (path, options = {}) => send(path, options)
