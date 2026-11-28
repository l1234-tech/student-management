# 86. Напишите CommonJS-скрипт Node.js, использующий console.log, process.nextTick, Promise.resolve().then, setTimeout(0) и setImmediate. Покажите ожидаемый гарантированный порядок первых трёх сообщений и отметьте, для каких двух callback порядок не гарантирован.

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

~~~js
console.log("sync");
process.nextTick(() => console.log("nextTick"));
Promise.resolve().then(() => console.log("promise"));
setTimeout(() => console.log("timeout"), 0);
setImmediate(() => console.log("immediate"));
~~~

В CommonJS первые три сообщения гарантированно идут sync → nextTick → promise: первый вызов синхронный, после стека Node обрабатывает очередь nextTick, затем Promise microtask. Порядок setTimeout(0) и setImmediate из главного скрипта не гарантирован и зависит от контекста/планирования. Внутри I/O callback setImmediate выполняется в check phase после poll. ESM может менять порядок nextTick и Promise. Источники — лекция 11 и [официальный материал Node](https://nodejs.org/en/learn/asynchronous-work/event-loop-timers-and-nexttick).
