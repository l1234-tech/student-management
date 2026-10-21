# Решение билета №1

## 1. Dependency Injection
DI передаёт объекту нужные зависимости извне, например через конструктор. В Spring контейнер создаёт и связывает компоненты. Если сервис сам создаёт конкретный репозиторий, он жёстко связан с реализацией и сложнее тестируется. При DI зависимость видна в конструкторе и может быть заменена тестовой. DI не запрещает любой new: речь о сервисных зависимостях и управлении ими.

~~~java
@Service
class StudentService {
    private final StudentRepository repository;
    StudentService(StudentRepository repository) {
        this.repository = repository;
    }
}
~~~

## 2. Интернет, Web, сайт, приложение и API
Интернет — глобальная сеть сетей. Web — система связанных ресурсов и приложений, доступных через веб-технологии. Сайт — набор связанных страниц/ресурсов под общим адресом. Веб-приложение позволяет пользователю выполнять действия и получать динамический результат. API — контракт взаимодействия программных компонентов; HTTP API может использовать браузер, мобильное приложение или другой сервер.

## 3. ES-модули

api.js:
~~~js
export async function loadStudents() {
  const response = await fetch("/api/students");
  if (!response.ok) {
    throw new Error("Не удалось загрузить студентов: HTTP " + response.status);
  }
  return await response.json();
}
~~~

main.js:
~~~js
import { loadStudents } from "./api.js";

async function main() {
  try {
    const students = await loadStudents();
    console.log(students);
  } catch (error) {
    console.error("Ошибка загрузки студентов:", error);
  }
}
main();
~~~

fetch возвращает Promise с объектом Response. Статусы 404/500 обычно не вызывают reject автоматически, поэтому проверяется response.ok. При ошибке throw отклоняет Promise функции; main ловит её через try/catch. response.json() тоже асинхронен и может завершиться ошибкой, если тело не является корректным JSON.