// Назначение: состояние списка, карточки, CRUD и серверной пагинации.
import { ref } from 'vue'
import { defineStore } from 'pinia'
import * as api from '../api/students'

export const useStudentsStore = defineStore('students', () => {
  const items = ref([])
  const selected = ref(null)
  const pending = ref(false)
  const error = ref(null)
  const pagination = ref({ page: 0, size: 20, totalElements: 0, totalPages: 0 })

  let listRequest = 0
  let cardRequest = 0

  async function load(filters = {}, signal) {
    const id = ++listRequest
    pending.value = true
    error.value = null

    try {
      const response = await api.listStudents(filters, signal)

      if (id === listRequest) {
        items.value = response?.data || []
        pagination.value = {
          page: response?.page ?? 0,
          size: response?.size ?? filters.size ?? 20,
          totalElements: response?.totalElements ?? 0,
          totalPages: response?.totalPages ?? 0,
        }
      }

      return response
    } catch (cause) {
      if (id === listRequest && cause.name !== 'AbortError') {
        error.value = cause
      }
      throw cause
    } finally {
      if (id === listRequest) {
        pending.value = false
      }
    }
  }

  async function loadOne(isu, signal) {
    const id = ++cardRequest
    selected.value = null
    pending.value = true
    error.value = null

    try {
      const student = await api.getStudent(isu, signal)

      if (id === cardRequest) {
        selected.value = student
      }

      return student
    } catch (cause) {
      if (id === cardRequest && cause.name !== 'AbortError') {
        error.value = cause
      }
      throw cause
    } finally {
      if (id === cardRequest) {
        pending.value = false
      }
    }
  }

  async function create(student) {
    pending.value = true
    error.value = null

    try {
      return await api.createStudent(student)
    } catch (cause) {
      error.value = cause
      throw cause
    } finally {
      pending.value = false
    }
  }

  async function update(isu, student) {
    pending.value = true
    error.value = null

    try {
      selected.value = await api.updateStudent(isu, student)
      return selected.value
    } catch (cause) {
      error.value = cause
      throw cause
    } finally {
      pending.value = false
    }
  }

  async function remove(isu, filters = {}) {
    error.value = null

    try {
      await api.deleteStudent(isu)
      const page =
        items.value.length === 1 && pagination.value.page > 0
          ? pagination.value.page - 1
          : pagination.value.page

      return await load({ ...filters, page, size: pagination.value.size })
    } catch (cause) {
      error.value = cause
      throw cause
    }
  }

  function clear() {
    listRequest += 1
    cardRequest += 1
    items.value = []
    selected.value = null
    pending.value = false
    error.value = null
    pagination.value = { page: 0, size: 20, totalElements: 0, totalPages: 0 }
  }

  return {
    items,
    selected,
    pending,
    error,
    pagination,
    load,
    loadOne,
    create,
    update,
    remove,
    clear,
  }
})
