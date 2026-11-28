# 88. Напишите два связанных компонента в выбранном метафреймворке: серверная страница загружает имя студента и передаёт его клиентскому компоненту, а клиентский компонент показывает имя и переключает видимость заметки по нажатию кнопки.

> Развёрнутый ответ из прежнего комплекта сохранён. Формулировка сверена с оригинальным PDF. Источники: лекции №2–11 и REST.pdf ([каталог](../../../../lectures/README.md)); дополнительные пояснения к API отмечены в ответе.

~~~tsx
// app/students/[id]/page.tsx
import NoteToggle from "./NoteToggle";

export default async function StudentPage({ params }) {
  const { id } = await params;
  const response = await fetch("https://api.example.test/students/" + id);
  if (response.status === 404) return <p>Студент не найден</p>;
  if (!response.ok) throw new Error("Ошибка загрузки");
  const student = await response.json();
  return <main><h1>{student.name}</h1><NoteToggle name={student.name} /></main>;
}
~~~

~~~tsx
// NoteToggle.tsx
"use client";
import { useState } from "react";

export default function NoteToggle({ name }) {
  const [visible, setVisible] = useState(false);
  return (
    <section>
      <p>{name}</p>
      <button onClick={() => setVisible(value => !value)}>
        {visible ? "Скрыть заметку" : "Показать заметку"}
      </button>
      {visible && <p>Заметка</p>}
    </section>
  );
}
~~~

Серверная страница извлекает id, получает запись и передаёт сериализуемое имя в клиентский компонент. Клиент отмечен директивой use client, потому что использует useState и обработчик. visible хранит boolean, setVisible инвертирует его; условные выражения меняют текст и видимость заметки. Секреты не отправляются клиенту. React-specific дополнение к лекции 10: [React Server Components](https://react.dev/reference/rsc/server-components).
