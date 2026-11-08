// Назначение: HTTP-операции авторизации по контракту AuthController из 3 lab.
import { request } from './http'

export const login = (credentials) =>
  request('/auth/login', { method: 'POST', body: credentials, auth: false })

export const register = (data) =>
  request('/auth/register', { method: 'POST', body: data, auth: false })

export const refresh = (refreshToken) =>
  request('/auth/refresh', {
    method: 'POST',
    body: { refreshToken },
    auth: false,
  })

export const logout = (refreshToken) =>
  request('/auth/logout', {
    method: 'POST',
    body: { refreshToken },
    auth: false,
  })

export const me = () => request('/auth/me')
