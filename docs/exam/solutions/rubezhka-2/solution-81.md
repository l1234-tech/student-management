# 81. Напишите минимальный HTTP-сервер на Node.js (node:http), который для GET /students/42 возвращает JSON-объект студента со статусом 200, для неизвестного id — 404, для неподдерживаемого метода — 405.

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

~~~js
const http = require("node:http");
const student = { id: 42, name: "Анна Иванова", group: "M3201" };

const server = http.createServer((request, response) => {
  if (request.method !== "GET") {
    response.writeHead(405, { "Content-Type": "application/json" });
    response.end(JSON.stringify({ error: "Method Not Allowed" }));
    return;
  }
  if (request.url === "/students/42") {
    response.writeHead(200, { "Content-Type": "application/json" });
    response.end(JSON.stringify(student));
    return;
  }
  response.writeHead(404, { "Content-Type": "application/json" });
  response.end(JSON.stringify({ error: "Not Found" }));
});
server.listen(3000, () => console.log("Listening on 3000"));
~~~

http — встроенный модуль Node. createServer принимает обработчик каждого запроса. request содержит method и url; response задаёт статус/заголовки и завершает тело. Не-GET получает 405. Известный URL получает JSON студента и 200, прочие — 404. writeHead задаёт статус, end закрывает ответ, JSON.stringify сериализует объект. listen открывает порт 3000. Обработчик должен оставаться коротким, чтобы не блокировать Event Loop (лекция 11).
