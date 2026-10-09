package dev.geeks.studentmanagement.auth.dto;

import jakarta.validation.constraints.NotBlank;

// DTO обновления access-токена через refresh-токен.
public record RefreshTokenRequest(
        @NotBlank(message = "refreshToken обязателен")
        String refreshToken
) {
}
