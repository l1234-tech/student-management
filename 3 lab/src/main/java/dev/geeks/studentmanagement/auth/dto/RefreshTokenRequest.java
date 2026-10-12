package dev.geeks.studentmanagement.auth.dto;

import jakarta.validation.constraints.NotBlank;

// =============================================================================
// Компонент:   RefreshTokenRequest (record)
// Модуль:      dev.geeks.studentmanagement.auth.dto
// Назначение:  входной DTO запроса POST /api/auth/refresh.
// Поля:        refreshToken — обязательное (@NotBlank).
// Статус: реализовано.
// =============================================================================

// DTO обновления access-токена через refresh-токен.
public record RefreshTokenRequest(
        @NotBlank(message = "refreshToken обязателен")
        String refreshToken
) {
}
