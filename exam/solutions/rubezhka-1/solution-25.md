# Рубежка №1 — решение билета 25

Материалы: [лекции](../../../lectures/README.md), [лабораторная №3](../../../3%20lab/DEFENSE.md).

## Теория 1. Spring Security filter chain

Запрос проходит через security filters до вызова Spring MVC контроллера. Фильтры извлекают credentials, выполняют аутентификацию, заполняют SecurityContext и проверяют правила доступа. Поэтому неавторизованный запрос обычно блокируется до входа в метод контроллера.

- SecurityFilterChain задаёт последовательность фильтров и правила безопасности.
- AuthenticationManager координирует аутентификацию через подходящий provider.
- SecurityContext хранит результат аутентификации для текущего контекста обработки.

Контроллер не должен самостоятельно проверять пароль в каждом методе; безопасность должна быть централизована и применяться на backend.

## Теория 2. Почему нельзя хранить пароль открыто или просто SHA-256

Открытый пароль сразу раскрывается при утечке БД. Быстрый хеш SHA-256 предназначен для скорости, поэтому атакующий может быстро проверить огромное число вариантов. Пароли имеют ограниченную энтропию и часто повторяются между сервисами.

Password hashing для паролей использует соль и намеренно медленный/настраиваемый алгоритм, например BCrypt или Argon2id. Соль делает одинаковые пароли разными по хешу и мешает использовать заранее рассчитанные таблицы. Проверка выполняется специальным PasswordEncoder, который учитывает соль и параметры алгоритма. Хеширование не обратимо как шифрование, а соль не является секретом.

## Практика. SecurityFilterChain для JWT и ролей

Пример Spring Security 6; JWT-фильтр/Resource Server должен быть настроен в проекте:

```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf -> csrf.disable()) // только если API использует bearer-токен, не cookie auth
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.GET, "/students/**")
                .hasAnyRole("USER", "ADMIN")
            .requestMatchers("/students/**")
                .hasRole("ADMIN")
            .anyRequest().authenticated())
        .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()))
        .build();
}
```

Сначала более узкое правило GET, затем общее правило для изменения студентов. В Spring `hasRole("ADMIN")` обычно ожидает authority `ROLE_ADMIN`. Stateless отключает серверную HTTP-сессию. CSRF можно отключать только если аутентификация действительно основана на токене, который клиент передаёт явно в Authorization и который браузер не прикладывает автоматически. Если используется cookie-based auth, CSRF-защита нужна. Импорты и JWT decoder зависят от конфигурации конкретного проекта.