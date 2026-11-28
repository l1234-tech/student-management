# 85. Напишите небольшой HTTP-обработчик Node.js: с помощью node:fs/promises асинхронно прочитайте students.json, преобразуйте массив студентов методом map и верните JSON-ответ. Добавьте обработку ошибки чтения; в комментариях отметьте операции JavaScript и API Node.js.

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

~~~js
const http = require("node:http");
const { readFile } = require("node:fs/promises");

const server = http.createServer(async (req, res) => {
  try {
    const text = await readFile("./students.json", "utf8"); // Node async file API
    const students = JSON.parse(text);                      // JavaScript parse
    const result = students.map(student => ({               // JavaScript transform
      id: student.id,
      displayName: student.name + " — " + student.group
    }));
    res.writeHead(200, { "Content-Type": "application/json" });
    res.end(JSON.stringify(result));                        // Node response + JS serialization
  } catch (error) {
    console.error(error);
    res.writeHead(500, { "Content-Type": "application/json" });
    res.end(JSON.stringify({ error: "Не удалось прочитать файл" }));
  }
});
server.listen(3000);
~~~

readFile — Node API, асинхронно читает диск; await ожидает Promise, не занимая поток синхронным ожиданием. JSON.parse — JavaScript преобразование строки в структуру данных. map строит новый массив представлений, исходный не меняет. writeHead/end — HTTP API Node; JSON.stringify сериализует данные. catch обрабатывает ошибку чтения или JSON и возвращает 500. В production стоит проверять URL и форму данных.
