package dev.geeks.studentmanagement.auth.dto;

import jakarta.validation.constraints.NotBlank;

// =============================================================================
// Компонент:   LoginRequest (record)
// Модуль:      dev.geeks.studentmanagement.auth.dto
// Назначение:  входной DTO запроса POST /api/auth/login.
// Поля:        username, password — обязательные (@NotBlank).
// Статус: реализовано.
// =============================================================================

// DTO входа пользователя: username и пароль.
public record LoginRequest(
        @NotBlank(message = "username обязателен")
        String username,

        @NotBlank(message = "password обязателен")
        String password
) {
}
