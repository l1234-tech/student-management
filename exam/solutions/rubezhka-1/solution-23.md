# Рубежка №1 — решение билета 23

## Условия из исходного PDF «Рубежка №1.pdf»

### Теоретический вопрос 45
> Сравните constructor injection, field injection и setter injection. Почему внедрение через конструктор часто удобнее для обязательных зависимостей и тестирования?

### Теоретический вопрос 46
> Чем подход к маршрутизации HTTP в Spring MVC отличается от Jakarta REST? Покажите смысл @RestController/@GetMapping и @Path/@GET без перечисления всех аннотаций подряд.

### Практическое задание 23
> На Java/Spring напишите метод register(password), который возвращает значение, безопасное для хранения вместо пароля, и покажите необходимую конфигурацию. Хранение в БД не требуется.

---


Материалы: [лекции](../../../lectures/README.md), [лабораторная №3](../../../3%20lab/DEFENSE.md).

## Теория 1. Constructor, field и setter injection

- Constructor injection передаёт зависимости при создании объекта; обязательные зависимости можно сделать final, а неполный объект невозможно создать обычным вызовом конструктора.
- Field injection записывает зависимости в поля после создания объекта; зависимость менее заметна, а unit-тесты сложнее без контейнера/reflection.
- Setter injection передаёт зависимость через метод; полезен для необязательных или перенастраиваемых зависимостей, но допускает промежуточное неинициализированное состояние.

Для обязательных зависимостей constructor injection обычно яснее и проще для тестирования. Это согласуется с DI в лабораторной №3.

## Теория 2. Spring MVC и Jakarta REST

Spring MVC использует, например, `@RestController`, `@RequestMapping`, `@GetMapping` и `@PathVariable`. Jakarta REST использует `@Path`, `@GET`, `@PathParam`. Оба подхода позволяют связать HTTP-метод и путь с Java-методом; различаются API и контейнерная экосистема.

`@RestController` — комбинация controller semantics и сериализации возвращаемого значения в тело ответа. Это не означает, что Spring MVC и Jakarta REST — одно и то же.

## Практика. Безопасное хранение пароля через BCrypt

```java
@Configuration
class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

@Service
class RegistrationService {
    private final PasswordEncoder passwordEncoder;

    RegistrationService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String register(String password) {
        return passwordEncoder.encode(password);
    }
}
```

Метод возвращает хеш BCrypt, а не исходный пароль. BCrypt автоматически использует соль и параметр стоимости. При входе следует вызывать `passwordEncoder.matches(rawPassword, storedHash)`, а не хешировать строку вручную и сравнивать хеши. Пароль и хеш нельзя помещать в логи или JWT. В Spring Security лабораторной применяется PasswordEncoder для хранения паролей.

## Прямые теоретические ответы

**Прямой ответ на вопросы 45–46.** Constructor injection передаёт зависимости в конструктор и делает обязательные зависимости видимыми; объект нельзя создать без них. Field injection записывает зависимость прямо в поле и меньше загромождает код, но скрывает требования класса и усложняет unit-тесты без контейнера. Setter injection полезен для опциональных или изменяемых после создания зависимостей, но позволяет получить объект без них, если это не контролировать. Поэтому конструктор обычно лучше для обязательных зависимостей.

Spring MVC использует аннотации Spring, такие как `@RestController` и `@GetMapping`, чтобы сопоставлять HTTP-запросы с методами контроллера и сериализовать возвращаемое значение. Jakarta REST — спецификация Java с аннотациями вроде `@Path` и `@GET`; реализации вроде Jersey обрабатывают эту модель. Оба подхода строят REST API, но принадлежат разным API/экосистемам и не являются взаимозаменяемыми аннотациями.
