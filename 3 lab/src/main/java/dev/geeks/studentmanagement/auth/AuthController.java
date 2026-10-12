package dev.geeks.studentmanagement.auth;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.MethodArgumentNotValidException;

import dev.geeks.studentmanagement.auth.dto.LoginRequest;
import dev.geeks.studentmanagement.auth.dto.RefreshTokenRequest;
import dev.geeks.studentmanagement.auth.dto.RegisterRequest;
import dev.geeks.studentmanagement.auth.dto.TokenResponse;
import dev.geeks.studentmanagement.common.error.ApiError;
import dev.geeks.studentmanagement.common.exception.ConflictException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

// =============================================================================
// Компонент:   AuthController
// Модуль:      dev.geeks.studentmanagement.auth
// Назначение:  REST-контроллер аутентификации.
// Маршруты:    POST /api/auth/register (201), /api/auth/login (200),
//              /api/auth/refresh (200).
// Обработка:   ошибки в едином формате ApiError — 409, 401, 422.
// Зависимости: AuthService, DTO авторизации, ApiError.
// Статус: реализовано.
// =============================================================================

// REST controller авторизации: регистрация, вход, обновление токена.
@Tag(name = "Auth", description = "JWT-авторизация: регистрация, вход, обновление токенов")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 201: пользователь создан, возвращены access и refresh токены.
    @Operation(summary = "Регистрация пользователя",
            description = "Создаёт пользователя и выдаёт пару access/refresh токенов. Роль ADMIN доступна только первому пользователю системы.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь создан, возвращены токены"),
            @ApiResponse(responseCode = "409", description = "Username или email уже заняты"),
            @ApiResponse(responseCode = "422", description = "Ошибка валидации входных данных")
    })
    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody RegisterRequest request) {
        TokenResponse tokens = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(tokens);
    }

    // 200: логин и пароль проверены, возвращены access и refresh токены.
    @Operation(summary = "Вход по логину и паролю",
            description = "Проверяет учётные данные и возвращает пару access/refresh токенов.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Учётные данные подтверждены, возвращены токены"),
            @ApiResponse(responseCode = "401", description = "Неверный логин или пароль"),
            @ApiResponse(responseCode = "422", description = "Ошибка валидации входных данных")
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // 200: refresh-токен проверен, выдан новый access-токен (и новый refresh-токен).
    @Operation(summary = "Обновление токенов",
            description = "Проверяет refresh-токен и выдаёт новую пару access/refresh токенов.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Токены обновлены"),
            @ApiResponse(responseCode = "401", description = "Недействительный или просроченный refresh-токен"),
            @ApiResponse(responseCode = "422", description = "Ошибка валидации входных данных")
    })
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request.refreshToken()));
    }

    // Локальные обработчики возвращают единый формат ApiError (common/error/ApiError).
    // Модуль common/error (GlobalExceptionHandler) пока реализует Маст: когда он появится,
    // локальный обработчик приоритетнее, формат ошибки при этом не меняется.
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex, HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, "CONFLICT", ex.getMessage(), request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        return buildError(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", ex.getMessage(), request);
    }

    // Ошибки Bean Validation: 422 и единый формат ApiError со списком полей и сообщений.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> details = new LinkedHashMap<>();
        details.put("path", String.valueOf(request.getRequestURI()));
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            details.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        ApiError error = new ApiError("VALIDATION_ERROR", "Ошибка валидации входных данных",
                HttpStatus.UNPROCESSABLE_ENTITY.value(), Instant.now(), details);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    private ResponseEntity<ApiError> buildError(HttpStatus status, String code, String message, HttpServletRequest request) {
        ApiError error = new ApiError(code, message, status.value(), Instant.now(),
                Map.of("path", String.valueOf(request.getRequestURI())));
        return ResponseEntity.status(status).body(error);
    }
}
