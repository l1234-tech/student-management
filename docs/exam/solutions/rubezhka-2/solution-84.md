# 84. Напишите обработчик Node.js для маршрутов /about и /profile. Для /about установите Cache-Control: public, max-age=60, а для /profile — Cache-Control: private, no-store. Верните разные JSON-ответы и обработайте неизвестный путь статусом 404.

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

~~~js
const http = require("node:http");
const server = http.createServer((req, res) => {
  if (req.method !== "GET") {
    res.writeHead(405);
    return res.end();
  }
  if (req.url === "/about") {
    res.writeHead(200, {
      "Content-Type": "application/json",
      "Cache-Control": "public, max-age=60"
    });
    return res.end(JSON.stringify({ page: "about" }));
  }
  if (req.url === "/profile") {
    res.writeHead(200, {
      "Content-Type": "application/json",
      "Cache-Control": "private, no-store"
    });
    return res.end(JSON.stringify({ page: "profile" }));
  }
  res.writeHead(404, { "Content-Type": "application/json" });
  res.end(JSON.stringify({ error: "Not Found" }));
});
server.listen(3000);
~~~

about разрешает публичному кешу хранить ответ 60 секунд. private запрещает shared cache использовать персональный профиль; no-store запрещает его сохранять. Обработчик сначала проверяет метод, затем путь. writeHead задаёт HTTP-статус и headers; end посылает тело и завершает ответ. Персональный ответ нельзя кешировать публично. Кеширование и Cache-Control описаны в лекции 10.
