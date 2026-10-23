# Рубежка №1 — решение билета 27

## Условия из исходного PDF «Рубежка №1.pdf»

### Теоретический вопрос 53
> undefined

### Теоретический вопрос 54
> undefined

### Практическое задание 27
> На JavaScript напишите async function updateProfile(profile, csrfToken), отправляющую POST /profile с JSON-телом и защитой от CSRF (сервер ждёт токен в заголовке X-CSRF-Token, аутентификация — по cookie). Если HTTP-ответ неуспешен, выбрасывайте Error.

---


Материалы: [лабораторная №3](../../../3%20lab/DEFENSE.md), [лабораторная №2](../../../1-2%20lab/DEFENSE_LAB2.md).

## Теория 1. Аутентификация и авторизация

Аутентификация отвечает на вопрос «кто ты?» — например, проверяет логин/пароль или JWT. Авторизация отвечает на вопрос «что тебе разрешено?». Успешный вход не означает, что пользователь имеет право удалять студентов.

В проекте роли USER и ADMIN определяют доступ к операциям. Проверка выполняется на backend; скрытая кнопка в UI — только удобство интерфейса и не защита от ручного HTTP-запроса.

## Теория 2. JPA, Hibernate и Spring Data JPA

JPA — спецификация Java Persistence API: аннотации и контракт для отображения объектов на реляционные данные. Hibernate и EclipseLink — реализации ORM-провайдера. Spring Data JPA добавляет репозиторный слой и генерацию стандартных операций/запросов.

В нашем проекте используется EclipseLink/JPA и PostgreSQL. Вызов метода репозитория приводит к работе ORM-провайдера, который формирует SQL, отправляет его через JDBC и преобразует строки результата в объекты. ORM уменьшает объём ручного маппинга, но не отменяет SQL, индексы, транзакции или стоимость запросов.

## Практика. POST с CSRF-токеном и cookie-аутентификацией

```js
async function updateProfile(profile, csrfToken) {
  const response = await fetch("/profile", {
    method: "POST",
    credentials: "same-origin",
    headers: {
      "Content-Type": "application/json",
      "Accept": "application/json",
      "X-CSRF-Token": csrfToken
    },
    body: JSON.stringify(profile)
  });

  if (!response.ok) {
    throw new Error(`HTTP error: ${response.status}`);
  }
  return response;
}
```

Cookie обычно отправляется браузером автоматически; `credentials: "same-origin"` явно разрешает credentials для same-origin запроса. CSRF-токен передаётся отдельным заголовком согласно контракту сервера. Проверка `response.ok` необходима, потому что fetch не бросает исключение для обычных HTTP-ответов 4xx/5xx. Если успешный ответ содержит JSON, можно вернуть `await response.json()`; условие не требует этого.