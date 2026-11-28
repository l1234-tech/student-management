# 54. Event Loop в Node.js: основные фазы и роль микрозадач.

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

Основные фазы лекции 11: **timers** (setTimeout/setInterval), **pending callbacks**, **poll** (I/O), **check** (setImmediate), **close callbacks**. JS callbacks исполняются по очереди в основном потоке. Очереди process.nextTick и Promise microtasks — не фазы libuv; они обрабатываются между callback/фазами.

В CommonJS nextTick обычно идёт перед Promise microtask. С libuv 1.45/Node.js 20 timers выполняются после poll; порядок setTimeout(0) и setImmediate из главного скрипта не гарантирован. Таймер задаёт минимальный порог, не точное время. Источники: лекция 11 и [документация Node Event Loop](https://nodejs.org/en/learn/asynchronous-work/event-loop-timers-and-nexttick).
