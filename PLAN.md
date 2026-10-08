# План выполнения лабораторной работы №3

Третья лабораторная переносит backend системы управления студентами из Django/DRF лабораторной 2 на Spring Boot с JPA и JWT-авторизацией, ролями ADMIN/USER и REST-API для студентов.

## Цель

- Сохранить REST API студентов из лабораторной 2.
- Перевести хранение данных на реляционную БД.
- Добавить регистрацию, авторизацию, JWT access/refresh.
- Ввести разграничение доступа по ролям на backend.
- Оставить единый формат ошибок, валидацию, пагинацию, фильтрацию, сортировку.
- Документировать API через Swagger/OpenAPI и подготовить Bruno/Postman-коллекцию.

## Стек и окружение

### Андрей
- Согласовать стек БД и настройки окружения, в том числе переменные:
  - `SERVER_PORT`
  - `DB_URL`
  - `DB_USERNAME`
  - `DB_PASSWORD`
  - `SPRING_PROFILES_ACTIVE`
  - `JWT_ACCESS_SECRET`
  - `JWT_REFRESH_SECRET`
  - `JWT_ACCESS_TTL_MINUTES`
  - `JWT_REFRESH_TTL_DAYS`
- Связать `application.yml`, `application-dev.yml` и `application-prod.yml`.
- Поддержать единый OpenAPI-контракт.
- Проверить, что параметры окружения вынесены в `.env.example` и настоящие `.env` не попадают в репозиторий.

### Маст
- Согласовать стек БД: Postgres по `application.yml`/`application-dev.yml`, или локальная H2 для быстрого запуска.
- Определить модели студента и конвертацию DTO/Entity.
- Определить поля студента: `ISU`, `name`, `group`, `dormitory`, `room`, `checkinDate`, `isForeign`, `note`.
- Определить DTO, валидацию DTO и бизнес-правила, например проверку уникальности ISU.
- Убедиться, что ошибки вызываются централизованно через `ApiError`.

## Кто отвечает за что

### Андрей
Модули: `security`, `auth`, `config`, `common/error`.

Файлы:
- `3 lab/src/main/java/dev/geeks/studentmanagement/security/JwtService.java`
- `3 lab/src/main/java/dev/geeks/studentmanagement/security/JwtAuthenticationFilter.java`
- `3 lab/src/main/java/dev/geeks/studentmanagement/config/SecurityConfig.java`
- `3 lab/src/main/java/dev/geeks/studentmanagement/config/OpenApiConfig.java`
- `3 lab/src/main/java/dev/geeks/studentmanagement/auth/AuthController.java`
- `3 lab/src/main/java/dev/geeks/studentmanagement/auth/AuthService.java`
- `3 lab/src/main/java/dev/geeks/studentmanagement/auth/model/User.java`
- `3 lab/src/main/java/dev/geeks/studentmanagement/auth/repository/UserRepository.java`
- `3 lab/src/main/java/dev/geeks/studentmanagement/common/error/ApiError.java`

Задачи:
- Реализовать генерацию access-токена в `JwtService`.
- Реализовать генерацию refresh-токена в `JwtService`.
- Реализовать проверку access-токена в `JwtService`.
- Реализовать проверку refresh-токена в `JwtService`.
- Реализовать чтение `username` и ролей из токена в `JwtService`.
- Наследоваться от `OncePerRequestFilter` в `JwtAuthenticationFilter`.
- В `JwtAuthenticationFilter` достать `Bearer token` из `Authorization`.
- В `JwtAuthenticationFilter` проверить JWT и положить `Authentication` в `SecurityContext`.
- В `SecurityConfig` описать `SecurityFilterChain`.
- В `SecurityConfig` разрешить отдачу `/api/auth/register`, `/api/auth/login`, `/api/auth/refresh` и Swagger без токена.
- В `SecurityConfig` разрешить `GET` для `/api/requests/**` от ролей `USER` и `ADMIN`.
- В `SecurityConfig` разрешить `POST`, `PATCH`, `PUT`, `DELETE` для `/api/requests/**` только от роли `ADMIN`.
- В `SecurityConfig` настроить `PasswordEncoder` или авто-конфигурацию, если она не выбрана явно.
- В `OpenApiConfig` добавить `OpenApi` bean.
- В `OpenApiConfig` указать название проекта, описание API и JWT security scheme.
- В `AuthController` реализовать `POST /api/auth/register` — создать пользователя и вернуть токены.
- В `AuthController` реализовать `POST /api/auth/login` — проверить пароль и вернуть токены.
- В `AuthController` реализовать `POST /api/auth/refresh` — проверить refresh token и выдать новый access token.
- В `AuthService` хешировать пароль через `PasswordEncoder`.
- В `AuthService` проверять уникальность `username` и `email`.
- В `AuthService` создавать пользователя с ролём `USER` или `ADMIN`.
- В `AuthService` хранить только хеш пароля, never plaintext.
- В `User` добавить `@Entity` и `@Table`.
- В `User` добавить `id`, `username`, `email`, `passwordHash`, `role`.
- В `User` добавить уникальные ограничения на `username` и `email`.
- В `UserRepository` наследоваться от `JpaRepository<User, Long>`.
- В `UserRepository` добавить `findByUsername`, `existsByUsername`, `existsByEmail`.
- В `ApiError` оставить единый формат ошибок API.

## Маст
Модули: `student`, `dto`, `common/error`, `config`, `bruno`, `static`, `templates`, `docs`, `README`.

Файлы:
- `3 lab/src/main/java/dev/geeks/studentmanagement/common/error/GlobalExceptionHandler.java`
- `3 lab/src/main/java/dev/geeks/studentmanagement/common/exception/ConflictException.java`
- `3 lab/src/main/java/dev/geeks/studentmanagement/common/exception/NotFoundException.java`
- `3 lab/src/main/java/dev/geeks/studentmanagement/student/StudentController.java`
- `3 lab/src/main/java/dev/geeks/studentmanagement/student/StudentService.java`
- `3 lab/src/main/java/dev/geeks/studentmanagement/student/mapper/StudentMapper.java`
- `3 lab/src/main/java/dev/geeks/studentmanagement/student/model/Student.java`
- `3 lab/src/main/java/dev/geeks/studentmanagement/student/repository/StudentRepository.java`
- `3 lab/src/main/java/dev/geeks/studentmanagement/student/dto/StudentCreateRequest.java`
- `3 lab/src/main/java/dev/geeks/studentmanagement/student/dto/StudentUpdateRequest.java`
- `3 lab/src/main/java/dev/geeks/studentmanagement/student/dto/StudentSearchRequest.java`
- `3 lab/src/main/java/dev/geeks/studentmanagement/student/dto/PageResponse.java`
- `3 lab/src/main/resources/application.yml`
- `3 lab/src/main/resources/application-dev.yml`
- `3 lab/src/main/resources/application-prod.yml`
- `3 lab/src/main/resources/templates/index.html`
- `3 lab/src/main/resources/templates/student.html`
- `3 lab/src/main/resources/templates/student_form.html`
- `3 lab/src/main/resources/static/css/style.css`
- `3 lab/src/main/resources/static/js/index.js`
- `3 lab/src/main/resources/static/js/student.js`
- `3 lab/src/main/resources/static/js/student_form.js`
- `3 lab/src/main/resources/static/js/validation.js`
- `3 lab/pom.xml`
- `3 lab/.env.example`
- `3 lab/README.md`
- `3 lab/docs/lab-3.md`
- `3 lab/bruno/README.md`
- `3 lab/bruno/bruno.json`
- `3 lab/bruno/collections/students.collection.json`
- `3 lab/bruno/collections/auth.collection.json`

Задачи:
- В `GlobalExceptionHandler` добавить `@RestControllerAdvice`.
- В `GlobalExceptionHandler` обработать `MethodArgumentNotValidException` и вернуть `422`.
- В `GlobalExceptionHandler` обработать `NotFoundException` и вернуть `404`.
- В `GlobalExceptionHandler` обработать `ConflictException` и вернуть `409`.
- В `GlobalExceptionHandler` обработать остальные ошибки в едином формате `ApiError`.
- В `ConflictException` и `NotFoundException` оставить исключения для конфликта и отсутствия ресурса.
- В `StudentController` реализовать `GET /api/requests` — список, фильтрация, сортировка, пагинация.
- В `StudentController` реализовать `QUERY /api/requests` — фильтрация через JSON body.
- В `StudentController` реализовать `GET /api/requests/{isu}` — получить одного студента.
- В `StudentController` реализовать `POST /api/requests` — создать студента, только `ADMIN`.
- В `StudentController` реализовать `PUT /api/requests/{isu}` — полное обновление, только `ADMIN`.
- В `StudentController` реализовать `PATCH /api/requests/{isu}` — частичное обновление, только `ADMIN`.
- В `StudentController` реализовать `DELETE /api/requests/{isu}` — удалить студента, только `ADMIN`.
- В `StudentService` реализовать `list(StudentSearchRequest request)` — список с фильтрацией, сортировкой и пагинацией.
- В `StudentService` реализовать `getByIsu(Integer isu)` — получить студента или бросить `NotFoundException`.
- В `StudentService` реализовать `create(StudentCreateRequest request)` — создать студента или бросить `ConflictException`.
- В `StudentService` реализовать `replace(Integer isu, StudentCreateRequest request)` — полная замена через `PUT`.
- В `StudentService` реализовать `patch(Integer isu, StudentUpdateRequest request)` — частичное обновление через `PATCH`.
- В `StudentService` реализовать `delete(Integer isu)` — удалить студента.
- В `StudentMapper` реализовать `toDto(Student student)`.
- В `StudentMapper` реализовать `fromCreateRequest(StudentCreateRequest request)`.
- В `StudentMapper` реализовать `updateEntity(Student student, StudentUpdateRequest request)`.
- В `Student` добавить `@Entity` и `@Table`.
- В `Student` добавить поля: `ISU`, `name`, `group`, `dormitory`, `room`, `checkinDate`, `isForeign`, `note`.
- В `Student` настроить уникальность `ISU`.
- В `StudentRepository` наследоваться от `JpaRepository<Student, Integer>` или `JpaRepository<Student, Long>`.
- В `StudentRepository` добавить `findByIsu`, `existsByIsu`, `deleteByIsu`.
- В `StudentRepository` реализовать сложную фильтрацию через `Specification` или `Criteria API`.
- В `StudentCreateRequest` добавить валидацию Bean Validation.
- В `StudentUpdateRequest` добавить валидацию Bean Validation.
- В `StudentSearchRequest` добавить поля фильтрации, сортировки и пагинации.
- В `PageResponse` оставить универсальный DTO ответа для пагинации.
- В `application.yml` настроить основную конфигурацию приложения.
- В `application-dev.yml` добавить локальную разработку.
- В `application-prod.yml` добавить продакшн-окружение.
- В `pom.xml` убедиться, что подключены нужные зависимости: Spring Web, Security, Data JPA, Validation, JPA-провайдер, драйвер БД, jjwt, springdoc-openapi, тесты.
- В `pom.xml` убедиться, что в dev-окружении доступна H2 или Postgres, и что `hibernate.ddl-auto` не мешает запуску.
- В `.env.example` перечислить переменные окружения для запуска.
- В `README.md` описать структуру проекта, запуск и API.
- В `docs/lab-3.md` добавить полное описание реализации.
- В `bruno` подготовить коллекцию запросов для сценариев: регистрация, авторизация, обновление токена, чтение студентов, создание/редактирование/удаление администратором, проверка прав `USER`, фильтрацию, сортировку и пагинацию.
- В `static` и `templates` перенести/адаптировать фронтенд: список студентов, форма, досье, валидацию, JWT и роли.
- В `3 lab/pom.xml` убедиться, что в `dev` запускается локальная БД без внешнего Postgres, если это предусмотрено.
- В `3 lab/README.md` убрать описание работы с `.env.example`, если оно уже занесено в `documentation`.
- В `3 lab/docs/lab-3.md` добавить инструкцию по запуску и проверке.
- В `3 lab/bruno/README.md` добавить инструкцию по запуску коллекции.
- В `3 lab/bruno/bruno.json` добавить структуру коллекции.
- В `3 lab/bruno/collections/students.collection.json` добавить запросы по студентам.
- В `3 lab/bruno/collections/auth.collection.json` добавить запросы по авторизации и refresh.

## Что делать перед началом работы

- Андрей:
  - Согласовать стек БД и настройки окружения.
  - Связать `application.yml`, `application-dev.yml` и `application-prod.yml`.
  - Поддержать единый OpenAPI-контракт.
- Маст:
  - Согласовать стек БД: Postgres по `application.yml`/`application-dev.yml`, или локальная H2 для быстрого запуска.
  - Определить модели студента и конвертацию DTO/Entity.
  - Определить поля студента: `ISU`, `name`, `group`, `dormitory`, `room`, `checkinDate`, `isForeign`, `note`.
  - Определить DTO, валидацию DTO и бизнес-правила, например проверку уникальности ISU.
  - Убедиться, что ошибки вызываются централизованно через `ApiError`.

## Порядок реализации

### Шаг 1. Архитектура и конфигурация
- Андрей:
  - Связать `application.yml`, `application-dev.yml` и `application-prod.yml`.
  - Поддержать единый OpenAPI-контракт.
- Маст:
  - В `pom.xml` убедиться, что подключены нужные зависимости.
  - В `application.yml` настроить основную конфигурацию.
  - В `application-dev.yml` добавить локальную разработку.
  - В `application-prod.yml` добавить продакшн-окружение.

### Шаг 2. Безопасность и авторизация
- Андрей:
  - Реализовать `JwtService`.
  - Реализовать `JwtAuthenticationFilter`.
  - Реализовать `SecurityConfig`.
  - Реализовать `OpenApiConfig`.
  - Реализовать `AuthController`.
  - Реализовать `AuthService`.
  - Реализовать `User`.
  - Реализовать `UserRepository`.
- Маст:
  - Реализовать `GlobalExceptionHandler`.
  - Убедиться, что `ApiError` используется как единый формат ошибок.

### Шаг 3. Студенты
- Маст:
  - Реализовать `Student` сущность.
  - Реализовать `StudentRepository`.
  - Реализовать `StudentService`.
  - Реализовать `StudentController`.
  - Реализовать `StudentMapper`.
  - Реализовать DTO: `StudentCreateRequest`, `StudentUpdateRequest`, `StudentSearchRequest`, `PageResponse`.
  - Реализовать валидацию DTO и бизнес-правила, например проверку уникальности ISU.

### Шаг 4. Фронтенд и документация
- Маст:
  - Перенести/адаптировать фронтенд: `index.html`, `student.html`, `student_form.html`, `static/css/style.css`, `static/js/index.js`, `static/js/student.js`, `static/js/student_form.js`, `static/js/validation.js`.
  - Обновить `README.md`.
  - Обновить `docs/lab-3.md`.
  - Обновить `bruno/README.md`.
  - Создать `bruno/bruno.json`.
  - Создать `bruno/collections/students.collection.json`.
  - Создать `bruno/collections/auth.collection.json`.

## Совместные проверки

### Перед началом
- Проверить, что `application.yml`, `application-dev.yml` и `application-prod.yml` связаны и не противоречат друг другу.
- Проверить, что OpenAPI-схема покрывает регистрацию, вход, refresh, авторизованные и неавторизованные запросы.

### При реализации
- Проверить, что DTO отвечает за внешний контракт API и не содержит JPA-сущности.
- Проверить, что `StudentDto`, `PageResponse`, `ApiError` и `TokenResponse` совпадают в сигнатурах.
- Проверить, что роли `ADMIN` и `USER` используются в токене, `SecurityConfig` и фронтенде единообразно.

### При проверке
- Проверить регистрацию, вход, refresh.
- Проверить защищённые запросы с `Authorization: Bearer <accessToken>`.
- Проверить: `USER` не может создавать, редактировать и удалять студентов.
- Проверить: `ADMIN` может выполнить все операции со студентами.
- Проверить пагинацию, фильтрацию, сортировку и поиск через `/api/requests`.
- Проверить `/swagger-ui/index.html` и Bruno-коллекцию.

## Как запускать и проверять

### Локальный запуск разработки
```bash
cd "3 lab"
cp .env.example .env
# отредактировать .env под локальную БД
mvn spring-boot:run
```

Swagger доступен на:
```text
http://localhost:8080/swagger-ui/index.html
```

### Локальный запуск через IDE
- Запустить `StudentManagementApplication`.
- Убедиться, что подключение к БД вынесено в переменные окружения через `.env.example`.
- Если используется H2, убедиться, что сборка и запуск не зависят от внешнего PostgreSQL.

### Проверка сборки и тестов
```bash
cd "3 lab"
mvn -q clean verify
```

### Проверка сборки по отдельным модулям
```bash
cd "3 lab"
mvn -q clean compile
mvn -q test
```

## Подводные камни

1. **JPA и `hibernate.ddl-auto: validate`.**
   - При `ddl-auto: validate` Spring Boot будет проверять, что сущности и схема совпадают с конфигурацией.
   - Если не добавить `@Entity`, `@Id`, `@Column` и ограничения, запуск упадёт.
   - В dev-окружении рекомендуется использовать `update` или `validate` только после полной привязки схемы.

2. **Spring Boot 3.3.4 и Spring Framework 6.**
   - Активы, аннотации и пакеты нужно использовать из `jakarta.*`, а не `javax.*`.
   - `io.jsonwebtoken` версии 0.12.x уже использует `jakarta.`.

3. **JWT-фильтр должен работать в правильной конфигурации:**
   - Если фильтр работает поверх Spring Security, то он не сможет увидеть заголовок `Authorization` на запросах `/api/auth/...`.
   - Нужно настроить `SecurityFilterChain` так, чтобы фильтры и URL-правила работали вместе.

4. **Роли проверяются на backend:**
   - Скрытие кнопок на фронтенде недостаточно.
   - `USER` должен отсутствовать доступ к `POST`, `PUT`, `PATCH`, `DELETE` в `SecurityConfig`.

5. **`GlobalExceptionHandler` без `@RestControllerAdvice`:**
   - Если в классе нет `@RestControllerAdvice`, исключения не будут централизованно преобразовываться в `ApiError`.
   - Нужно добавить `@RestControllerAdvice` и обработать `MethodArgumentNotValidException`, `NotFoundException`, `ConflictException`, общие `Exception`.

6. **Уникальность полей:**
   - `ISU` в API должен быть уникальным для студентов.
   - `username` и `email` должны быть уникальными для пользователей.
   - Проверку нужно делать на уровне сервиса и/или БД через `existsByIsu`, `existsByUsername`, `existsByEmail` и исключения `ConflictException`.

7. **Пагинация и сортировка:**
   - Нельзя возвращать всего список без предела.
   - Нужно отдельно возвращать `page`, `size`, `totalElements`, `totalPages`.
   - Сортировку нужно ограничивать допустимыми полями и направлениями.

8. **DTO от JPA-сущностей:**
   - `StudentDto` не должен содержать `@Entity` или `EntityManager`.
   - Использовать `StudentMapper` для преобразования.
   - API не должен ничего знать о внутренней модели.

9. **Фронтенд и JWT:**
   - `static/js/index.js`, `student.js`, `student_form.js`, `validation.js` и шаблоны должны отправлять токен по `Authorization`.
   - `401` на frontend нужно обрабатывать перезапросом с `refreshToken`.
   - Интерфейс должен скрывать изменение данных для `USER`.

10. **Bruno/Postman:**
   - В `bruno/README.md` уже перечислены сценарии, но сам файл `bruno.json` и коллекции отсутствуют.
   - Сборку нужно проверить локально.

## Файлы, которые нужно создать или подготовить

### JSON
- `3 lab/bruno/bruno.json`
- `3 lab/bruno/collections/students.collection.json`
- `3 lab/bruno/collections/auth.collection.json`

### Ресурсы
- `3 lab/src/main/resources/application-dev.yml`
- `3 lab/src/main/resources/application-prod.yml`
- `3 lab/src/main/resources/templates/index.html`
- `3 lab/src/main/resources/templates/student.html`
- `3 lab/src/main/resources/templates/student_form.html`
- `3 lab/src/main/resources/static/css/style.css`
- `3 lab/src/main/resources/static/js/index.js`
- `3 lab/src/main/resources/static/js/student.js`
- `3 lab/src/main/resources/static/js/student_form.js`
- `3 lab/src/main/resources/static/js/validation.js`
- `3 lab/.env.example`

## Итог

- `application.yml` настроен на конфигурацию.
- `application-dev.yml` — для локальной разработки.
- `application-prod.yml` — для продакшн-окружения.
- Бэкенд запускается на порту `8080` по умолчанию.
- Swagger доступен на `/swagger-ui/index.html`.
- Документация и коллекция запросов оформлены в `bruno/`.
- Код разделён по пакетам: `auth`, `student`, `common`, `config`, `security`.
- Пароли хранятся только как хэши.
- Доступ управляется только на backend по ролям.
- REST API остаётся удобным для клиента.
