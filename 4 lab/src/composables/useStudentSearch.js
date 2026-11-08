// Назначение: RxJS-поиск с debounce, отменой fetch и восстановлением потока после ошибок.
import { EMPTY, Observable, Subject } from 'rxjs'
import { catchError, debounceTime, distinctUntilChanged, finalize, switchMap } from 'rxjs/operators'
import { onUnmounted, ref } from 'vue'

export function useStudentSearch(searchFn) {
  const query = ref({})
  const pending = ref(false)
  const error = ref(null)
  const changes = new Subject()

  const subscription = changes
    .pipe(
      debounceTime(300),
      distinctUntilChanged((a, b) => JSON.stringify(a) === JSON.stringify(b)),
      switchMap((filters) =>
        new Observable((observer) => {
          const controller = new AbortController()
          pending.value = true
          error.value = null

          Promise.resolve(searchFn(filters, controller.signal))
            .then((result) => {
              observer.next(result)
              observer.complete()
            })
            .catch((cause) => {
              if (cause.name === 'AbortError') {
                observer.complete()
              } else {
                observer.error(cause)
              }
            })

          return () => controller.abort()
        }).pipe(
          catchError((cause) => {
            error.value = cause
            return EMPTY
          }),
          finalize(() => {
            pending.value = false
          }),
        ),
      ),
    )
    .subscribe()

  function setQuery(filters) {
    query.value = { ...filters }
    changes.next(query.value)
  }

  onUnmounted(() => {
    subscription.unsubscribe()
    changes.complete()
  })

  return { query, pending, error, setQuery }
}
