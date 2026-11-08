# Лабораторная №4 — Student Management (Vue 3)

Frontend реализован как SPA на Vue 3 Composition API. Используются Vite, Vue Router, Pinia и RxJS. Backend повторно используется из лабораторной №3.

## Структура

- src/pages — список, карточка, создание, редактирование, вход, регистрация и профиль.
- src/components — таблица, поиск, фильтры, пагинация, форма и общие состояния.
- src/stores — глобальное состояние Pinia для сессии и студентов.
- src/api — HTTP-клиент, авторизация и REST API студентов.
- src/composables/useStudentSearch.js — RxJS-поиск с debounce и отменой запросов.
- src/router — маршруты SPA и проверка ролей.
- Dockerfile, nginx.conf, compose.yaml — сборка и совместный запуск.

## Локальный запуск

Нужны Git и Node.js 24+ с npm. Из корня репозитория:

```powershell
cd "4 lab"
Copy-Item .env.example .env
npm.cmd ci
npm.cmd run dev
```

Откройте http://localhost:5173. Vite проксирует /api на API_PROXY_TARGET (по умолчанию http://localhost:8080). Backend лабораторной №3 запускается отдельно по инструкции в ../3 lab/README.md.

Сборка frontend: `npm.cmd run build`.

## Авторизация

| Операция | Метод и путь | Результат |
| --- | --- | --- |
| Регистрация | POST /api/auth/register | 201, роль USER и пара JWT |
| Вход | POST /api/auth/login | Пара JWT |
| Обновление | POST /api/auth/refresh | Новая пара JWT, прежний refresh отзывается |
| Выход | POST /api/auth/logout | 204, отзыв refresh-сессии |
| Текущий пользователь | GET /api/auth/me | username и role |

Токены хранятся в sessionStorage: переживают обновление страницы и удаляются при закрытии вкладки. Это хранилище доступно JavaScript, поэтому XSS остаётся риском. Для HttpOnly Secure cookies потребуются изменения backend.

HTTP-клиент использует один общий refresh Promise для параллельных 401, повторяет защищённый запрос не более одного раза и не запускает refresh для auth endpoints. 403 не очищает сессию. При окончательном отказе refresh токены и данные студентов очищаются. При сетевой ошибке восстановления сохранённые токены остаются для повторной попытки.

Регистрация всегда создаёт USER. Роль ADMIN назначается только серверной конфигурацией. Профиль показывает username и role: backend не предоставляет endpoint изменения профиля.

## API студентов

| Операция | Метод и путь | Доступ |
| --- | --- | --- |
| Список и фильтры | POST /api/user/students/search | USER, ADMIN |
| Карточка | GET /api/user/students/{isu} | USER, ADMIN |
| Создание | POST /api/admin/students | ADMIN |
| Редактирование | PUT /api/admin/students/{isu} | ADMIN |
| Удаление | DELETE /api/admin/students/{isu} | ADMIN |

Фильтрация поддерживает ISU, name, group, dormitory, room, is_foreign, checkin_date и note; условия объединяются через AND до пагинации. Пустые значения не отправляются, boolean false сохраняется. Сортировка — field,asc или field,desc; page начинается с нуля. Ответ списка содержит data, page, size, totalElements и totalPages. Ответ карточки обёрнут в data, удаление возвращает 204.

RxJS-поиск использует debounceTime(300), distinctUntilChanged и switchMap. Предыдущий fetch отменяется через AbortController; ошибка отдельного запроса не завершает поток.

## Docker Compose

Нужен Docker Desktop с Compose. Из папки 4 lab создайте .env из .env.example и задайте непустой DB_PASSWORD. Для создания администратора заполните ADMIN_USERNAME, ADMIN_EMAIL и ADMIN_PASSWORD; оставьте их пустыми, чтобы не создавать ADMIN.

```powershell
docker compose config --quiet
docker compose up --build
```

Сайт доступен на http://localhost:8084. Compose запускает frontend Nginx, Spring Boot backend из 3 lab и отдельную PostgreSQL. Nginx раздаёт dist, возвращает index.html для вложенных SPA-маршрутов и проксирует /api/ на backend:8080 с сохранением префикса.

Обычный docker compose down сохраняет volumes. Не используйте --volumes, если нужно сохранить данные. Не коммитьте заполненный .env — в Git хранится только .env.example.

## Проверки перед сдачей

```powershell
npm.cmd ci
npm.cmd run build
docker compose config --quiet
docker compose up --build
```

Проверьте прямой reload вложенного URL, регистрацию, вход, восстановление сессии, refresh, logout, роли USER/ADMIN, запрет USER на запись, фильтры включая false, сортировку, пагинацию, 404 карточки, 204 удаления и сетевые ошибки. Контейнерный запуск считается проверенным только после фактического запуска Docker.
