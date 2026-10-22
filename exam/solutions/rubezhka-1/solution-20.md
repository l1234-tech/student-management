# Рубежка №1 — решение билета 20

Материалы: [лабораторная №2](../../../1-2%20lab/DEFENSE_LAB2.md), [лабораторная №3](../../../3%20lab/DEFENSE.md).

## Теория 1. WSGI и ASGI

WSGI — синхронный интерфейс между Python web-приложением и сервером. Он хорошо подходит для обычных запросов, обрабатываемых синхронными обработчиками. ASGI — асинхронный интерфейс, который поддерживает асинхронные события и длительные соединения, например WebSocket.

Интерфейс приложения должен соответствовать серверу развёртывания. ASGI не делает любой код асинхронным: синхронный блокирующий вызов всё ещё может задерживать event loop.

## Теория 2. I/O-bound и CPU-bound

I/O-bound задача большую часть времени ждёт ввод-вывод: сеть, диск, базу данных. Асинхронность позволяет в период ожидания обслуживать другие задачи. CPU-bound задача тратит время на вычисления; `await` не уменьшает количество вычислений и сам по себе не ускоряет её.

Пример I/O-bound — параллельные запросы к API; пример CPU-bound — обработка большого массива чистым Python. Для CPU-bound может понадобиться multiprocessing, нативный код или распределённые вычисления.

## Практика. Spring Service с constructor injection

```java
@Service
public class StudentService {
    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public Student get(long id) {
        return repository.findById(id)
            .orElseThrow(() -> new StudentNotFoundException(id));
    }
}
```

Spring создаёт StudentService и передаёт ему StudentRepository. Единственный конструктор в Spring обычно может использоваться для внедрения без `@Autowired`. `Optional<Student>` выражает, что запись может отсутствовать; `orElseThrow` превращает отсутствие в понятное исключение, которое контроллер/глобальный handler должен отобразить в HTTP 404. Исключение `StudentNotFoundException` здесь предполагается существующим или создаётся отдельно.