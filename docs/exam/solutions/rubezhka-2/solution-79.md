# 79. Напишите модуль StudentApi на JavaScript/TypeScript с методами list(), getById(id), create(data). Все методы должны вызывать общую функцию request(), проверяющую response.ok и преобразующую успешный ответ из JSON.

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

~~~ts
type Student = { isuId: number; name: string; group: string };

async function request(url, options = {}) {
  const response = await fetch(url, options);
  if (!response.ok) throw new Error("HTTP " + response.status);
  if (response.status === 204) return null;
  return response.json();
}

export const StudentApi = {
  list(): Promise<Student[]> {
    return request("/api/students");
  },
  getById(id: number): Promise<Student> {
    return request("/api/students/" + encodeURIComponent(id));
  },
  create(data: Omit<Student, "isuId">): Promise<Student> {
    return request("/api/students", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data)
    });
  }
};
~~~

Student — тип записи. request централизует fetch, проверку response.ok и JSON-разбор; 204 возвращает null без чтения тела. list получает массив, getById кодирует id, create отправляет POST с JSON-заголовком и сериализованным телом. Omit исключает isuId из данных создания, если его назначает сервер. Вызывающий код должен учесть null для 204; в реальном API можно также нормализовать ошибки 401/403/404.
