package dev.geeks.studentmanagement.auth.dto;

import jakarta.validation.constraints.NotBlank;

// DTO входа пользователя: username и пароль.
public record LoginRequest(
        @NotBlank(message = "username обязателен")
        String username,

        @NotBlank(message = "password обязателен")
        String password
) {
}
