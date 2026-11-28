# 77. Напишите два фрагмента RxJS-кода: (а) combineLatest для реактивной фильтрации students$ по значению groupFilter$; (б) forkJoin для одновременного получения профиля и настроек через два одноразовых HTTP-запроса.

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

~~~ts
import { combineLatest, forkJoin, from } from "rxjs";
import { map } from "rxjs/operators";

const filteredStudents$ = combineLatest([students$, groupFilter$]).pipe(
  map(([students, group]) =>
    group ? students.filter(student => student.group === group) : students
  )
);

const dashboard$ = forkJoin({
  profile: from(fetch("/api/profile").then(r => r.json())),
  settings: from(fetch("/api/settings").then(r => r.json()))
});
~~~

combineLatest ждёт по первому значению каждого потока, затем при любом обновлении пересчитывает. students$ — массив, groupFilter$ — критерий; map возвращает отфильтрованный список. forkJoin ждёт завершения всех конечных Observable и выдаёт один объект с последними значениями. from превращает Promise fetch в Observable. Ошибка любого источника завершит forkJoin ошибкой, если её не обработать. Источник — лекция 9 и [RxJS forkJoin](https://rxjs.dev/api/index/function/forkJoin).
