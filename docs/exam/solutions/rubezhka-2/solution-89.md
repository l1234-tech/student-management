# 89. Напишите серверный middleware/guard для двух маршрутов: без авторизованной сессии запрос к /profile перенаправляется на /login, а пользователь с ролью USER при запросе к /admin получает 403. Для ADMIN запрос к /admin должен проходить дальше.

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

~~~js
const express = require("express");
const app = express();

// Предполагается, что проверенный session middleware установлен выше.
function requireSession(req, res, next) {
  if (!req.session?.user) return res.redirect("/login");
  next();
}
function requireAdmin(req, res, next) {
  if (req.session?.user?.role !== "ADMIN") {
    return res.status(403).json({ error: "Нет прав" });
  }
  next();
}
app.get("/profile", requireSession, (req, res) => {
  res.json({ name: req.session.user.name });
});
app.get("/admin", requireSession, requireAdmin, (req, res) => {
  res.json({ page: "admin" });
});
~~~

req содержит HTTP-запрос, res формирует ответ, next передаёт выполнение дальше. requireSession проверяет серверную сессию: без пользователя делает redirect, с пользователем вызывает next. requireAdmin проверяет роль; USER получает 403, ADMIN проходит до обработчика. Сессия должна быть проверена middleware ранее, нельзя доверять роли из пользовательского поля. Backend API также обязан проверять права. Тема middleware — лекция 10.
