# 83. Напишите два JavaScript-модуля ES Modules: students.mjs экспортирует функцию findStudentById(students, id), а main.mjs импортирует её, вызывает для массива из двух студентов и выводит найденного студента. Используйте import и export.

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

~~~js
// students.mjs
export function findStudentById(students, id) {
  return students.find(student => student.id === id);
}
~~~

~~~js
// main.mjs
import { findStudentById } from "./students.mjs";
const students = [
  { id: 1, name: "Анна Иванова" },
  { id: 2, name: "Борис Петров" }
];
const found = findStudentById(students, 2);
console.log(found);
~~~

export публикует именованную функцию, фигурные скобки import выбирают это имя. findStudentById принимает массив и id; find возвращает совпавший объект либо undefined. students — тестовые записи, found — результат. .mjs включает режим ES Modules в Node. Запуск: node main.mjs.
