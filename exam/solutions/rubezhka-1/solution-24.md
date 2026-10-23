# Рубежка №1 — решение билета 24

## Условия из исходного PDF «Рубежка №1.pdf»

### Теоретический вопрос 47
> Почему singleton/ application-scoped бин не следует использовать для хранения изменяемых данных текущего запроса? Как область видимости влияет на число экземпляров и возможные гонки?

### Теоретический вопрос 48
> Чем динамический Proxy, BeanPostProcessor и CDI-интерцептор различаются по роли в системе? Почему их нельзя считать тремя независимыми названиями одного механизма?

### Практическое задание 24
> На JavaScript напишите async function requestWithRefresh(url, access, refresh): сделайте GET с Authorization: Bearer access; если получен 401, вызовите POST /auth/refresh с JSON {refresh}, возьмите из ответа новый access и повторите исходный GET ровно один раз. Иные ошибки не обрабатывайте. Считайте, что refresh endpoint возвращает JSON с полем access.

---


Материалы: [лекции](../../../lectures/README.md), [лабораторная №3](../../../3%20lab/DEFENSE.md).

## Теория 1. Scope и изменяемое состояние

Singleton bean обычно один на Spring ApplicationContext. Если хранить в нём поля, которые изменяются для каждого HTTP-запроса, несколько потоков могут одновременно читать и менять одно состояние, что создаёт гонки и утечку данных между пользователями. Singleton подходит для stateless-сервиса с безопасными зависимостями, но не для «текущего пользователя» в обычном изменяемом поле.

Scope определяет жизненный цикл и число экземпляров. Request scope создаёт экземпляр на запрос, но не делает автоматически безопасными любые внешние общие ресурсы. Данные пользователя следует брать из корректного request/security context.

## Теория 2. Proxy, BeanPostProcessor и CDI interceptor

Динамический Proxy — техника создания объекта-заместителя, перехватывающего вызовы интерфейса/методов. BeanPostProcessor — расширение жизненного цикла Spring bean, которое может обработать экземпляр и вернуть прокси. CDI interceptor — механизм Jakarta CDI для перехвата вызовов по interceptor bindings.

Это разные уровни: Proxy — способ реализации перехвата; BeanPostProcessor — hook контейнера Spring; CDI interceptor — модель интерсепции в CDI. В Spring proxy может быть создан инфраструктурой, в том числе post-processor, поэтому они не являются тремя независимыми синонимами.

## Практика. Обновить access token и повторить GET один раз

```js
async function requestWithRefresh(url, access, refresh) {
  const sendGet = (token) => fetch(url, {
    method: "GET",
    headers: { Authorization: `Bearer ${token}` }
  });

  let response = await sendGet(access);
  if (response.status !== 401) return response;

  const refreshResponse = await fetch("/auth/refresh", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ refresh })
  });

  if (!refreshResponse.ok) {
    throw new Error(`Refresh failed: ${refreshResponse.status}`);
  }

  const data = await refreshResponse.json();
  return sendGet(data.access); // повтор ровно один раз
}
```

Первый GET отправляет access token. Только при 401 выполняется refresh. Новый access используется для одного повтора; если повтор снова вернёт 401, функция не запускает новый refresh, поэтому цикл бесконечно не повторяется. Другие HTTP-ошибки исходного GET возвращаются вызывающему коду как Response; это соответствует условию «иные ошибки не обрабатывайте».

## Прямые теоретические ответы

**Прямой ответ на вопросы 47–48.** Singleton/application-scoped бин обычно разделяется многими запросами. Если хранить в его изменяемом поле текущий ID пользователя или данные запроса, параллельные запросы могут перезаписывать значение друг друга. Это создаёт гонки и риск утечки данных между пользователями. Состояние запроса следует хранить в request context или локальных переменных вызова; общее изменяемое состояние требует продуманной синхронизации и общего хранилища при нескольких процессах.

Динамический Proxy — объект-обёртка, перехватывающий вызовы интерфейса/класса. BeanPostProcessor — расширение жизненного цикла Spring для обработки бинов, в том числе формирования proxy. CDI-interceptor — механизм Jakarta CDI для выполнения сквозного поведения вокруг вызова метода по interceptor binding. Они могут взаимодействовать в задачах вроде транзакций, но находятся на разных уровнях: Proxy — техника перехвата вызова, BeanPostProcessor — hook контейнера Spring, CDI-interceptor — программная модель перехватчиков CDI.
