package dev.geeks.studentmanagement.auth.dto;

// DTO входа пользователя: username/email и пароль.
public record LoginRequest(
        String username,
        String password
) {
}
