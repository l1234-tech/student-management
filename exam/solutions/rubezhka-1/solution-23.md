# Рубежка №1 — решение билета 23

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