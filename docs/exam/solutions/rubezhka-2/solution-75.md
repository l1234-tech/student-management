# 75. Напишите функцию requestJson(url, options) поверх fetch: при 401 перенаправьте на /login, при 403 выбросьте ошибку «Нет прав», при 404 — «Не найдено», при 500/5xx — «Ошибка сервера», а при успехе верните JSON; учтите ответ 204 без тела.

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

~~~ts
async function requestJson(url, options = {}) {
  const response = await fetch(url, options);
  if (response.status === 401) {
    window.location.assign("/login");
    throw new Error("Требуется вход");
  }
  if (response.status === 403) throw new Error("Нет прав");
  if (response.status === 404) throw new Error("Не найдено");
  if (response.status >= 500) throw new Error("Ошибка сервера");
  if (!response.ok) throw new Error("HTTP " + response.status);
  if (response.status === 204) return null;
  return response.json();
}
~~~

url — адрес, options — метод/заголовки/тело. response — ответ Fetch API. 401 перенаправляет на вход и выбрасывает ошибку; 403 — нет прав, 404 — отсутствует ресурс, 5xx — серверная ошибка. response.ok ловит остальные коды ошибок. 204 успешен, но тела нет, поэтому json() вызывать нельзя. window доступен в браузере; для SSR redirect делают средствами метафреймворка.
