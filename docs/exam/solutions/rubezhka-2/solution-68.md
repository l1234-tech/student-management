# 68. Напишите обработчик deleteStudent(isuId): отправьте DELETE /api/students/{isuId}, при успехе удалите запись из реактивного массива иммутабельно, при ошибке покажите сообщение. Используйте выбранный frontend-фреймворк.

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

~~~ts
async function deleteStudent(isuId, students, errorMessage) {
  errorMessage.value = "";
  try {
    const response = await fetch(
      "/api/students/" + encodeURIComponent(isuId),
      { method: "DELETE" }
    );
    if (!response.ok) throw new Error("HTTP " + response.status);
    students.value = students.value.filter(
      student => student.isuId !== isuId
    );
  } catch {
    errorMessage.value = "Не удалось удалить студента";
  }
}
~~~

isuId — id удаляемой записи; students и errorMessage — реактивные refs. fetch отправляет DELETE; encodeURIComponent кодирует id для URL. response.ok сообщает успех 2xx. При ошибочном статусе возникает исключение, которое ловится и превращается в сообщение. filter создаёт новый массив без записи, а не мутирует старый. Если backend ответил 204, тело читать не надо. Реальную проверку прав выполняет сервер.
