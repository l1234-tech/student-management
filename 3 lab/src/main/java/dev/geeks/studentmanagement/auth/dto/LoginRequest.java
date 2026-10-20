// принимает и обрабатывает данные, которые клиент 
// отправляет на сервер при входе (попытке в общем случае)

package dev.geeks.studentmanagement.auth.dto;

import jakarta.validation.constraints.*;

public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
