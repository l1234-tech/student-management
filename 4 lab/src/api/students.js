// Назначение: REST API студентов через общий HTTP-клиент.
import { request } from './http'

function cleanFilters(filters) {
  return Object.fromEntries(
    Object.entries(filters).filter(
      ([, value]) => value !== '' && value !== null && value !== undefined,
    ),
  )
}

export function listStudents(filters = {}, signal) {
  return request('/user/students/search', {
    method: 'POST',
    body: cleanFilters(filters),
    signal,
  })
}

export async function getStudent(isu, signal) {
  return (await request(`/user/students/${encodeURIComponent(isu)}`, { signal }))?.data
}

export async function createStudent(student) {
  return (await request('/admin/students', { method: 'POST', body: student }))?.data
}

export async function updateStudent(isu, student) {
  return (
    await request(`/admin/students/${encodeURIComponent(isu)}`, {
      method: 'PUT',
      body: student,
    })
  )?.data
}

export function deleteStudent(isu) {
  return request(`/admin/students/${encodeURIComponent(isu)}`, { method: 'DELETE' })
}
