# Рубежка №1 — решение билета 14

Материалы: [лабораторная №2](../../../1-2%20lab/DEFENSE_LAB2.md), [лабораторная №3](../../../3%20lab/DEFENSE.md).

## Теория 1. MVC и Controller–Service–Repository

MVC разделяет Model, View и Controller. Model представляет данные/состояние и правила, View отвечает за представление, Controller координирует запрос пользователя. Схема Controller–Service–Repository — слоистая архитектура backend: контроллер HTTP, сервис бизнес-логики, репозиторий доступа к данным.

Это разные способы описания архитектуры, и они могут сочетаться. В REST API View часто не HTML-шаблон, а сериализованное представление ответа — например JSON/DTO. Не нужно утверждать, что «View всегда отдельный HTML-файл».

## Теория 2. Strategy, Factory Method и Adapter

- **Strategy** инкапсулирует взаимозаменяемые алгоритмы и позволяет выбрать один из них.
- **Factory Method** делегирует создание объекта методу/подклассу, чтобы клиент не зависел от конкретного класса создаваемого продукта.
- **Adapter** приводит интерфейс существующего класса к интерфейсу, который ожидает клиент.

Выбираем Strategy для смены алгоритма фильтрации/сортировки; Factory Method — когда нужно скрыть выбор конкретного объекта; Adapter — когда сторонняя библиотека имеет несовместимый интерфейс. Это не синонимы: различается цель изменения дизайна.

## Практика. FastAPI handler для StudentNotFound

```python
from fastapi import Request
from fastapi.responses import JSONResponse

@app.exception_handler(StudentNotFound)
async def student_not_found_handler(
    request: Request,
    exc: StudentNotFound
):
    return JSONResponse(
        status_code=404,
        content={"error": "student_not_found"}
    )
```

Декоратор регистрирует глобальный обработчик данного исключения. Когда оно выходит из endpoint, FastAPI вызывает handler, который возвращает HTTP 404 и JSON-тело. В реальном проекте следует определить, какие сведения безопасно показывать клиенту; внутренний traceback клиенту обычно не возвращают.