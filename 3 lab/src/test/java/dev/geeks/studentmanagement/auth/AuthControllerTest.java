package dev.geeks.studentmanagement.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import dev.geeks.studentmanagement.auth.dto.LoginRequest;
import dev.geeks.studentmanagement.auth.dto.RegisterRequest;
import dev.geeks.studentmanagement.auth.dto.TokenResponse;
import dev.geeks.studentmanagement.common.exception.ConflictException;

// Тесты AuthController: standalone MockMvc, AuthService заменён стабом
// (без Mockito — ByteBuddy не поддерживает JDK 25 в этой среде).
// Проверяются коды ответов, JSON-контракт токенов и формат ApiError.
class AuthControllerTest {

    private StubAuthService authService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        authService = new StubAuthService();
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService)).build();
    }

    // Стаб сервиса: по умолчанию возвращает фиксированные токены,
    // тесты могут назначить исключение для проверки обработчиков ошибок.
    private static class StubAuthService extends AuthService {
        RuntimeException registerError;
        RuntimeException loginError;
        RuntimeException refreshError;

        StubAuthService() {
            super(null, null, null);
        }

        @Override
        public TokenResponse register(RegisterRequest request) {
            if (registerError != null) {
                throw registerError;
            }
            return new TokenResponse("access-token", "refresh-token");
        }

        @Override
        public TokenResponse login(LoginRequest request) {
            if (loginError != null) {
                throw loginError;
            }
            return new TokenResponse("access-token", "refresh-token");
        }

        @Override
        public TokenResponse refresh(String refreshToken) {
            if (refreshError != null) {
                throw refreshError;
            }
            return new TokenResponse("new-access", "new-refresh");
        }
    }

    @Test
    void register_returns201_withTokens() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"ivan\",\"email\":\"ivan@test.ru\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    void register_invalidPayload_returns422_ApiError() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\",\"email\":\"bad\",\"password\":\"short\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void register_conflict_returns409_ApiError() throws Exception {
        authService.registerError = new ConflictException("Пользователь с таким username уже зарегистрирован");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"ivan\",\"email\":\"ivan@test.ru\",\"password\":\"password123\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLICT"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void login_returns200_withTokens() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"ivan\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"));
    }

    @Test
    void login_badCredentials_returns401_ApiError() throws Exception {
        authService.loginError = new BadCredentialsException("Неверный логин или пароль");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"ivan\",\"password\":\"wrong-pass\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void refresh_returns200_withNewTokens() throws Exception {
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"refresh-token\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh"));
    }

    @Test
    void refresh_blankToken_returns422_ApiError() throws Exception {
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }
}
