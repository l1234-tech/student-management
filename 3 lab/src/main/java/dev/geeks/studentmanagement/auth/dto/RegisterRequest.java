package dev.geeks.studentmanagement.auth.dto;

// DTO регистрации пользователя. Здесь нужны username, email, password и роль при необходимости.
public record RegisterRequest(
        String username,
        String email,
        String password
) {
}
