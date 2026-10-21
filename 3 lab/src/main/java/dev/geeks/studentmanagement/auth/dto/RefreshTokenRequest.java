// нужен для обновления токенов авторизации , описывает этот процесс

package dev.geeks.studentmanagement.auth.dto;

import jakarta.validation.constraints.*;

public record RefreshTokenRequest(@NotBlank String refreshToken) {}
