# 76. Напишите RxJS-код для поля поиска студентов: fromEvent(input, "input"), debounceTime(300), distinctUntilChanged() и switchMap для GET /api/students?q=... . Сделайте так, чтобы устаревший ответ не перезаписывал результаты последнего запроса.

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

~~~ts
import { fromEvent, from } from "rxjs";
import { debounceTime, distinctUntilChanged, map, switchMap } from "rxjs/operators";

const input = document.querySelector("#student-search");
const results$ = fromEvent(input, "input").pipe(
  map(() => input.value.trim()),
  debounceTime(300),
  distinctUntilChanged(),
  switchMap(query =>
    from(fetch("/api/students?q=" + encodeURIComponent(query))
      .then(response => {
        if (!response.ok) throw new Error("HTTP " + response.status);
        return response.json();
      }))
  )
);

const subscription = results$.subscribe({
  next: students => renderStudents(students),
  error: error => showError(error.message)
});
~~~

fromEvent создаёт поток событий; map получает строку. debounceTime ждёт 300 мс тишины, distinctUntilChanged пропускает только новое значение. switchMap отписывается от предыдущего внутреннего потока, поэтому устаревший ответ не попадёт в UI. from(Promise) не отменяет native fetch физически — он прекращает доставку результата в RxJS; для отмены сети применяют fromFetch/AbortController. renderStudents и showError — функции отображения.
