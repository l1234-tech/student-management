# Рубежка №1 — решение билета 21

Материалы: [лекции](../../../lectures/README.md), [лабораторная №3](../../../3%20lab/DEFENSE.md).

## Теория 1. Spring IoC container и beans

Spring обнаруживает компоненты через component scanning и конфигурацию, регистрирует определения bean, создаёт объекты, разрешает зависимости и выполняет этапы инициализации. Bean — объект, жизненным циклом которого управляет контейнер. Объект, созданный через `new`, сам по себе не становится Spring bean: контейнер не внедрит в него автоматически зависимости и не применит стандартные post-processors.

Аннотации `@Component`, `@Service`, `@Repository`, `@Controller` и `@RestController` помогают обнаруживать компоненты. В лабораторной №3 сервисы и контроллеры управляются Spring, а зависимости внедряются через конструктор.

## Теория 2. Конструктор, @PostConstruct и прокси

При constructor injection зависимости передаются в конструктор до создания полностью готового объекта, поэтому ими можно пользоваться в конструкторе. При field injection контейнер сначала создаёт объект, а затем внедряет поля; в конструкторе поля ещё могут быть null. `@PostConstruct` вызывается после внедрения зависимостей и до обычного использования bean.

Прокси — отдельный аспект жизненного цикла: Spring может обернуть bean для транзакций, безопасности или AOP. Не стоит смешивать создание объекта, внедрение полей, callback и вызов через прокси — это разные этапы. Для обязательных зависимостей обычно предпочтительнее constructor injection.

## Практика. Зафиксировать время запуска через Clock

```java
@Configuration
class TimeConfig {
    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}

@Service
class StudentService {
    private final Clock clock;
    private Instant startedAt;

    StudentService(Clock clock) {
        this.clock = clock;
    }

    @PostConstruct
    void initialize() {
        this.startedAt = Instant.now(clock);
    }

    public Instant getStartedAt() {
        return startedAt;
    }
}
```

Clock внедряется извне, что облегчает тестирование с фиксированным временем. `@PostConstruct` подходит для операции после внедрения. Нужны импорты `java.time.Clock`, `java.time.Instant`, `jakarta.annotation.PostConstruct` и Spring-аннотаций.