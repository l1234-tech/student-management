<!-- Назначение: страница списка, фильтров, RxJS-поиска и серверной пагинации. -->
<script setup>
import { computed, onMounted, reactive } from 'vue'
import { storeToRefs } from 'pinia'
import { RouterLink } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useStudentsStore } from '../stores/students'
import { useStudentSearch } from '../composables/useStudentSearch'
import StudentsTable from '../components/StudentsTable.vue'
import SearchInput from '../components/SearchInput.vue'
import StudentFilters from '../components/StudentFilters.vue'
import StudentPagination from '../components/StudentPagination.vue'
import LoadingIndicator from '../components/LoadingIndicator.vue'
import ErrorMessage from '../components/ErrorMessage.vue'

const store = useStudentsStore()
const auth = useAuthStore()
const { items, pagination, pending, error } = storeToRefs(store)

const filters = reactive({
  ISU: '',
  name: '',
  group: '',
  dormitory: '',
  room: '',
  checkin_date: '',
  is_foreign: null,
  note: '',
  sort: 'ISU,asc',
  page: 0,
  size: 20,
})

const {
  query,
  pending: searching,
  error: searchError,
  setQuery,
} = useStudentSearch((params, signal) => store.load(params, signal))

const isAdmin = computed(() => auth.user?.role === 'ADMIN')

function buildFilters(page = 0) {
  const result = {
    ...filters,
    name: filters.name.trim(),
    note: filters.note.trim(),
    page,
  }

  Object.keys(result).forEach((key) => {
    if (result[key] === '' || result[key] === null || result[key] === undefined) {
      delete result[key]
    }
  })

  return result
}

function apply() {
  filters.page = 0
  setQuery(buildFilters(0))
}

function changePage(page) {
  filters.page = page
  setQuery({ ...query.value, page })
}

function changeSize(size) {
  filters.size = size
  filters.page = 0
  setQuery(buildFilters(0))
}

function changeSort(field) {
  const [current, direction] = filters.sort.split(',')
  filters.sort = `${field},${current === field && direction === 'asc' ? 'desc' : 'asc'}`
  apply()
}

function reset() {
  Object.assign(filters, {
    ISU: '',
    name: '',
    group: '',
    dormitory: '',
    room: '',
    checkin_date: '',
    is_foreign: null,
    note: '',
    sort: 'ISU,asc',
    page: 0,
    size: 20,
  })
  apply()
}

async function remove(isu) {
  if (!isAdmin.value || !window.confirm('Удалить студента?')) {
    return
  }

  try {
    await store.remove(isu, query.value)
  } catch {
  }
}

onMounted(() => setQuery(buildFilters(0)))
</script>

<template>
  <section>
    <h2>Студенты</h2>
    <RouterLink v-if="isAdmin" to="/students/new">Добавить студента</RouterLink>
    <form @submit.prevent="apply">
      <SearchInput v-model="filters.name" />
      <StudentFilters
        :filters="filters"
        @update:filters="Object.assign(filters, $event)"
        @apply="apply"
        @reset="reset"
      />
      <button type="submit">Найти</button>
    </form>
    <LoadingIndicator :active="pending || searching" />
    <ErrorMessage :error="searchError || error" />
    <p v-if="!pending && !searching && !error && !items.length">
      Студенты не найдены.
    </p>
    <StudentsTable
      :students="items"
      :is-admin="isAdmin"
      @remove="remove"
      @sort="changeSort"
    />
    <StudentPagination
      v-bind="pagination"
      :pending="pending || searching"
      @change="changePage"
      @size-change="changeSize"
    />
  </section>
</template>
