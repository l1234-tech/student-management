# 5. Модули, Promise, async/await и Fetch API

## 5.1. Зачем нужны модули

**Модуль** — файл или единица кода с явно определёнными экспортами и импортами. Модули позволяют разделять программу на части, повторно использовать функции и не держать весь код в одном файле.

Файл `api.js` отвечает за взаимодействие с API:

```js
export async function loadStudents() {
  const response = await fetch("/api/students");

  if (!response.ok) {
    throw new Error(`Не удалось загрузить студентов: HTTP ${response.status}`);
  }

  return response.json();
}
```

Файл `main.js` использует эту функцию:

```js
import { loadStudents } from "./api.js";

loadStudents()
  .then((students) => console.log(students))
  .catch((error) => console.error(error));
```

- `export` делает объявление доступным другим модулям.
- `import` подключает экспорт из другого файла.
- `./api.js` — относительный путь от текущего модуля.
- Фигурные скобки в `import { loadStudents }` обозначают именованный импорт.

Именованный экспорт должен импортироваться под соответствующим именем, если не используется переименование. У модуля также может быть `export default`, который импортируется без фигурных скобок.

Чтобы браузер понял модульный синтаксис, HTML обычно подключает entry-файл так:

```html
<script type="module" src="./main.js"></script>
```

При открытии HTML напрямую через `file://` загрузка модулей и запросы часто ограничены политиками браузера. Для разработки лучше использовать локальный HTTP-сервер.

## 5.2. Синхронный и асинхронный код

**Синхронный код** выполняется последовательно: следующая инструкция ждёт завершения предыдущей. **Асинхронная операция** позволяет программе продолжить выполнение, пока ожидается внешний результат, например ответ сети или таймер.

Сетевой запрос может занять десятки или сотни миллисекунд, а иногда и дольше. Если бы интерфейс полностью блокировался до завершения каждой операции, он был бы неудобным.

JavaScript использует event loop и очередь задач для координации выполнения. Для экзамена важно понимать идею: ожидание сети не означает, что нужно блокировать весь интерфейс.

## 5.3. Promise

**Promise** — объект, представляющий результат асинхронной операции, который будет получен в будущем. Он может быть:
- `pending` — ожидание;
- `fulfilled` — успешно завершён;
- `rejected` — завершён с ошибкой.

```js
fetch("/api/students")
  .then((response) => response.json())
  .then((students) => console.log(students))
  .catch((error) => console.error(error));
```

- `then` регистрирует обработчик успешного результата и может преобразовать результат для следующего звена цепочки.
- `catch` обрабатывает отклонение Promise.
- `finally` выполняется после завершения независимо от успеха или ошибки, например чтобы скрыть индикатор загрузки.

В этом упрощённом примере не проверяется HTTP-статус. В реальном коде надо проверить `response.ok` до чтения JSON.

## 5.4. async и await

Функция, объявленная с `async`, всегда возвращает Promise. `await` приостанавливает выполнение внутри этой async-функции до завершения Promise, не блокируя весь браузер.

```js
async function loadStudents() {
  const response = await fetch("/api/students");

  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`);
  }

  const students = await response.json();
  return students;
}
```

Построчно:
1. `async function` объявляет асинхронную функцию.
2. `fetch` начинает HTTP-запрос и возвращает Promise.
3. `await` ждёт объект Response.
4. `response.ok` проверяет, находится ли статус в диапазоне 200–299.
5. `response.json()` асинхронно читает тело и разбирает JSON.
6. `return students` задаёт успешный результат Promise, возвращаемого функцией.

## 5.5. Важный нюанс Fetch API

`fetch()` обычно отклоняет Promise при сетевой ошибке, но **не отклоняет его автоматически из-за HTTP 404 или 500**. При этих статусах запрос успешно получил HTTP-ответ, который сообщает об ошибке. Поэтому проверка `response.ok` нужна отдельно.

```js
async function getJson(url) {
  const response = await fetch(url, {
    headers: { Accept: "application/json" }
  });

  if (!response.ok) {
    throw new Error(`Request failed: ${response.status}`);
  }

  return response.json();
}
```

Другие методы чтения ответа:
- `response.json()` — разобрать JSON;
- `response.text()` — получить текст;
- `response.blob()` — получить бинарные данные как Blob.

Тело ответа обычно нельзя прочитать несколько раз без клонирования, потому что поток потребляется.

## 5.6. POST-запрос с JSON

```js
async function createStudent(student) {
  const response = await fetch("/api/students", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Accept": "application/json"
    },
    body: JSON.stringify(student)
  });

  if (!response.ok) {
    throw new Error(`Не удалось создать студента: HTTP ${response.status}`);
  }

  return response.json();
}
```

- `method` задаёт HTTP-метод.
- `headers` задают метаданные.
- `JSON.stringify` преобразует объект JavaScript в JSON-строку.
- `body` содержит отправляемые данные.

Не путай `JSON.stringify(value)` (объект → JSON-текст) и `JSON.parse(text)` (JSON-текст → объект).

## 5.7. Обработка ошибок в вызывающем коде

```js
try {
  const students = await loadStudents();
  console.log(students);
} catch (error) {
  console.error("Ошибка загрузки", error);
}
```

Если функция вызывается из другой async-функции, можно использовать try/catch. Иначе можно использовать `.then(...).catch(...)`. Важно выбрать способ и обработать ошибку в подходящем месте — например показать пользователю сообщение и сохранить возможность повторить запрос.

## 5.8. Типичный шаблон для билета

Требование: экспортировать async-функцию `loadStudents()`, которая запрашивает `/api/students`, возвращает JSON и выбрасывает ошибку при неуспешном HTTP-статусе.

`api.js`:

```js
export async function loadStudents() {
  const response = await fetch("/api/students");

  if (!response.ok) {
    throw new Error(`HTTP error: ${response.status}`);
  }

  return await response.json();
}
```

`main.js`:

```js
import { loadStudents } from "./api.js";

loadStudents()
  .then((students) => console.log(students))
  .catch((error) => console.error(error));
```

Можно написать `return response.json()` без `await` в конце: async-функция вернёт этот Promise и его результат будет итоговым результатом. Вариант с `return await` здесь тоже корректен, но не обязателен.

## Самопроверка

1. Зачем нужны модули?
2. Чем именованный экспорт отличается от default export?
3. Что такое Promise и какие у него состояния?
4. Что делает await?
5. Почему fetch с HTTP 404 не обязательно попадает в catch?
6. Для чего response.ok?
7. Чем JSON.stringify отличается от response.json()?
8. Зачем у POST указывают Content-Type?
