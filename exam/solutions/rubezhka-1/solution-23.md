# Рубежка №1 — билет 23: точные условия и подробные решения

## Теоретический вопрос 45 — условие из файла

> Сравните constructor injection, field injection и setter injection. Почему внедрение через конструктор часто удобнее для обязательных зависимостей и тестирования?

### Подробный ответ

- **Constructor injection** — зависимости передаются параметрами конструктора. Объект не может быть создан без обязательных зависимостей, поля можно объявить `final`, а unit-тест способен передать mock/fake напрямую.
- **Field injection** — контейнер записывает зависимость в поле, часто через reflection. Код короче, но зависимость не видна в конструкторе, а обычный тест не может создать объект в полноценном состоянии без reflection или запуска контейнера.
- **Setter injection** — зависимость передаётся методом-setter после создания объекта. Подходит для необязательных или перенастраиваемых зависимостей, но допускает промежуточное состояние, когда объект ещё не настроен.

**Прямой ответ:** конструктор удобнее для обязательных зависимостей, потому что они видны в контракте класса и требуются при создании. Это улучшает тестируемость, облегчает неизменяемость полей и не позволяет незаметно использовать сервис без обязательного репозитория.

## Теоретический вопрос 46 — условие из файла

> Чем подход к маршрутизации HTTP в Spring MVC отличается от Jakarta REST? Покажите смысл @RestController/@GetMapping и @Path/@GET без перечисления всех аннотаций подряд.

### Подробный ответ

Spring MVC — веб-фреймворк Spring, в котором контроллеры обычно объявляются через `@Controller`/`@RestController`, а маршруты задаются Spring-аннотациями, например `@GetMapping`. `@RestController` объединяет семантику контроллера и возврата тела ответа; метод с `@GetMapping` связывается с GET-маршрутом.

Jakarta REST (ранее JAX-RS) — спецификация Java для REST ресурсов. Класс/метод ресурса обычно размечается `@Path`, `@GET`, а параметры берутся из соответствующих частей URI или запроса. Оба подхода позволяют сопоставлять HTTP-запросы с методами, но используют разные API и соглашения. Нельзя смешивать аннотации без соответствующей инфраструктуры.

## Практическое задание 23 — условие из файла

> На Java/Spring напишите метод register(password), который возвращает значение, безопасное для хранения вместо пароля, и покажите необходимую конфигурацию. Хранение в БД не требуется.

### Решение

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

Сервисный метод:

```java
@Service
public class RegistrationService {
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String register(String password) {
        return passwordEncoder.encode(password);
    }
}
```

### Подробный разбор

- Конфигурация создаёт управляемый Spring bean интерфейса `PasswordEncoder`.
- `BCryptPasswordEncoder` применяет адаптивный алгоритм хеширования пароля, включающий соль и вычислительную стоимость.
- `register` возвращает результат `encode(password)`, а не исходный пароль.
- При входе пароль проверяют методом `matches(rawPassword, storedHash)`, а не пытаются расшифровать хеш или сравнивать plaintext.

Нельзя сохранять пароль в открытом виде или использовать для него обычный быстрый SHA-256 без специализированной схемы password hashing: быстрый алгоритм облегчает массовый перебор украденных хешей. В условии не требуется работа с БД, поэтому метод лишь возвращает безопасное для хранения представление.
