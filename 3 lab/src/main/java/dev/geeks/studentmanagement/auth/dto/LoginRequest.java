package dev.geeks.studentmanagement.auth.dto;

import jakarta.validation.constraints.*;

public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
