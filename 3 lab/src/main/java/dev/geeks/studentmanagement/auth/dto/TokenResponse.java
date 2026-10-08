package dev.geeks.studentmanagement.auth.dto;

// DTO ответа после login/register/refresh: accessToken и refreshToken.
public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
