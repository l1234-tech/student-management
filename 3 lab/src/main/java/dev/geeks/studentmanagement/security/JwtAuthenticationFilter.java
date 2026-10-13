package dev.geeks.studentmanagement.security;

// Фильтр безопасности: читает Authorization: Bearer <token>, проверяет access token и авторизует пользователя.
public class JwtAuthenticationFilter {
    // TODO: Наследоваться от OncePerRequestFilter.
    // TODO: Достать Bearer token из заголовка.
    // TODO: Проверить JWT и положить Authentication в SecurityContext.
}
