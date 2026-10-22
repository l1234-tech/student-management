# Рубежка №1 — решение билета 24

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