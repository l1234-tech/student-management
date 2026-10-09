package dev.geeks.studentmanagement.auth.dto;

import dev.geeks.studentmanagement.auth.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// DTO регистрации пользователя: username, email, password и необязательная роль.
// role по умолчанию USER; ADMIN может получить только первый пользователь системы (см. AuthService).
public record RegisterRequest(
        @NotBlank(message = "username обязателен")
        @Size(min = 3, max = 100, message = "username должен содержать от 3 до 100 символов")
        String username,

        @NotBlank(message = "email обязателен")
        @Email(message = "email должен быть корректным адресом")
        @Size(max = 255, message = "email не должен превышать 255 символов")
        String email,

        @NotBlank(message = "password обязателен")
        @Size(min = 8, max = 72, message = "password должен содержать от 8 до 72 символов")
        String password,

        // Необязательное поле: null или USER - регистрируется читатель, ADMIN - только первому пользователю.
        Role role
) {
}
