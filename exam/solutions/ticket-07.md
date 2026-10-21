# Решение билета №7

## 1. JVM/JDK
JDK содержит средства разработки, включая компилятор javac. Компиляция Java создаёт байткод, который выполняется JVM. JVM может интерпретировать байткод и использовать JIT-компиляцию.

## 2. DI/IoC
IoC — общий принцип передачи управления инфраструктуре. DI — способ предоставлять объекту зависимости извне. Spring-контейнер создаёт компоненты и связывает их зависимости.

## 3. Практика
~~~java
@Service
public class StudentService {
    private final StudentRepository repository;
    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }
}
~~~
Контейнер Spring находит подходящий компонент StudentRepository и передаёт его конструктору. Сервис не привязывается к самостоятельному созданию конкретной реализации.