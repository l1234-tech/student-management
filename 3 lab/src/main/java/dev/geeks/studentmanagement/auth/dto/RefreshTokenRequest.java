package dev.geeks.studentmanagement.auth.dto;

// DTO обновления access-токена через refresh-токен.
public record RefreshTokenRequest(
        String refreshToken
) {
}
