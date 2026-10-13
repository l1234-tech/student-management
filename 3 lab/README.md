# Лабораторная 3. Spring Boot + JPA

<!-- В этом файле описана архитектура третьей лабораторной: что где реализовывать и как связать backend, БД, JWT и frontend. -->

Третья лабораторная переносит backend из Django-реализации лабораторной 2 на Java backend.

Вариант реализации:

- Spring Boot.
- JPA / EclipseLink или совместимая JPA-реализация по варианту преподавателя.
- Реляционная база данных.
- JWT авторизация с access- и refresh-токенами.
- Роли `ADMIN` и `USER`.

## Главная идея

REST API из лабораторной 2 сохраняется по смыслу:

```text
GET    /api/requests
GET    /api/requests/{isu}
POST   /api/requests
PUT    /api/requests/{isu}
PATCH  /api/requests/{isu}
DELETE /api/requests/{isu}
QUERY  /api/requests
```

Отличие от лабораторной 2:

- данные студентов хранятся не в JSON-файле, а в реляционной БД;
- появляется регистрация и авторизация пользователей;
- защищённые операции проверяются через JWT;
- права зависят от роли пользователя;
- API документируется через Swagger / OpenAPI.

## Права доступа

`ADMIN`:

- может смотреть список студентов;
- может смотреть досье студента;
- может создавать студентов;
- может редактировать студентов;
- может удалять студентов.

`USER`:

- может смотреть список студентов;
- может смотреть досье студента;
- не может создавать, изменять и удалять студентов.

Разграничение прав должно быть реализовано на backend, а не только скрытием кнопок во frontend.

## Структура проекта

```text
3 lab/
  pom.xml
  .env.example
  src/main/java/dev/geeks/studentmanagement/
    StudentManagementApplication.java
    auth/
      AuthController.java
      AuthService.java
      dto/
      model/
      repository/
    common/
      error/
      exception/
    config/
      OpenApiConfig.java
      SecurityConfig.java
    security/
      JwtAuthenticationFilter.java
      JwtService.java
    student/
      StudentController.java
      StudentService.java
      dto/
      mapper/
      model/
      repository/
  src/main/resources/
    application.yml
    static/
      css/
      js/
    templates/
  bruno/
    README.md
```

## Слои backend

Controller:

- принимает HTTP-запрос;
- вызывает service;
- возвращает DTO и HTTP-статус.

Service:

- содержит бизнес-логику;
- проверяет уникальность ИСУ;
- применяет фильтры, сортировку и пагинацию;
- проверяет правила, которые не относятся напрямую к HTTP.

Repository:

- работает с базой данных через JPA;
- не содержит бизнес-логику.

Entity:

- описывает таблицы базы данных.

DTO:

- описывает внешний контракт API;
- отделяет API от внутренней модели приложения.

Mapper:

- переводит Entity в DTO и обратно.

Security:

- выдаёт JWT;
- проверяет access-токен;
- обновляет access-токен через refresh-токен;
- кладёт пользователя в security context.

## Запуск

Перед запуском нужно создать `.env` по примеру `.env.example`.

Пример команд:

```powershell
cd "3 lab"
mvn spring-boot:run
```

Swagger после запуска:

```text
http://localhost:8080/swagger-ui/index.html
```

## Что ещё нужно реализовать

- Подключить реальную БД в `application.yml`.
- Реализовать JPA entity для студентов и пользователей.
- Реализовать Spring Security с JWT.
- Реализовать refresh-token сценарий.
- Реализовать CRUD студентов.
- Реализовать фильтрацию, сортировку и пагинацию.
- Реализовать роли `ADMIN` и `USER`.
- Подготовить Bruno или Postman коллекцию.
