package dev.geeks.studentmanagement.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import dev.geeks.studentmanagement.auth.dto.LoginRequest;
import dev.geeks.studentmanagement.auth.dto.RegisterRequest;
import dev.geeks.studentmanagement.auth.dto.TokenResponse;
import dev.geeks.studentmanagement.auth.model.Role;
import dev.geeks.studentmanagement.auth.model.User;
import dev.geeks.studentmanagement.auth.repository.UserRepository;
import dev.geeks.studentmanagement.common.exception.ConflictException;
import dev.geeks.studentmanagement.security.JwtService;

// Юнит-тесты AuthService.
// Вместо Mockito (ByteBuddy не поддерживает JDK 25 в этой среде) используются
// hand-written дублёры: in-memory репозиторий на java.lang.reflect.Proxy,
// реальный BCryptPasswordEncoder и подкласс JwtService-регистратор.
class AuthServiceTest {

    private Map<String, User> storage;
    private RecordingJwtService jwtService;
    private BCryptPasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        storage = new HashMap<>();
        jwtService = new RecordingJwtService();
        passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthService(inMemoryRepository(storage), passwordEncoder, jwtService);
    }

    // --- Тест-дублёры ---

    private static UserRepository inMemoryRepository(Map<String, User> storage) {
        return (UserRepository) Proxy.newProxyInstance(
                UserRepository.class.getClassLoader(),
                new Class<?>[]{UserRepository.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "findByUsername" -> Optional.ofNullable(storage.get(args[0]));
                    case "existsByUsername" -> storage.containsKey(args[0]);
                    case "existsByEmail" -> storage.values().stream()
                            .anyMatch(user -> user.getEmail().equals(args[0]));
                    case "count" -> (long) storage.size();
                    case "save" -> {
                        User user = (User) args[0];
                        storage.put(user.getUsername(), user);
                        yield user;
                    }
                    case "toString" -> "InMemoryUserRepository";
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    default -> defaultValue(method.getReturnType());
                });
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) {
            return null;
        }
        if (type == boolean.class) {
            return false;
        }
        if (type == long.class) {
            return 0L;
        }
        return 0;
    }

    private static class RecordingJwtService extends JwtService {
        boolean refreshValid;
        String refreshUser;

        RecordingJwtService() {
            super("unit-test-access-secret-0123456789abcdef",
                    "unit-test-refresh-secret-0123456789abcdefgh", 15, 30);
        }

        @Override
        public String generateAccessToken(String username, Role role) {
            return "access:" + username + ":" + role;
        }

        @Override
        public String generateRefreshToken(String username, Role role) {
            return "refresh:" + username + ":" + role;
        }

        @Override
        public boolean validateRefreshToken(String token) {
            return refreshValid;
        }

        @Override
        public String extractUsernameFromRefresh(String token) {
            return refreshUser;
        }
    }

    // --- Сценарии регистрации ---

    @Test
    void register_savesOnlyHash_andReturnsTokens() {
        TokenResponse tokens = authService.register(
                new RegisterRequest("ivan", "ivan@test.ru", "password123", null));

        User saved = storage.get("ivan");
        assertThat(saved).isNotNull();
        assertThat(saved.getPasswordHash()).isNotEqualTo("password123");
        assertThat(passwordEncoder.matches("password123", saved.getPasswordHash())).isTrue();
        assertThat(saved.getRole()).isEqualTo(Role.USER);
        assertThat(tokens.accessToken()).isEqualTo("access:ivan:USER");
        assertThat(tokens.refreshToken()).isEqualTo("refresh:ivan:USER");
    }

    @Test
    void register_duplicateUsername_throwsConflict() {
        storage.put("ivan", new User("ivan", "other@test.ru", "hash", Role.USER));

        assertThatThrownBy(() -> authService.register(
                new RegisterRequest("ivan", "ivan@test.ru", "password123", null)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("username");
        assertThat(storage).hasSize(1);
    }

    @Test
    void register_duplicateEmail_throwsConflict() {
        storage.put("root", new User("root", "ivan@test.ru", "hash", Role.USER));

        assertThatThrownBy(() -> authService.register(
                new RegisterRequest("ivan", "ivan@test.ru", "password123", null)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("email");
        assertThat(storage).hasSize(1);
    }

    @Test
    void register_firstUser_requestedAdmin_receivesAdmin() {
        TokenResponse tokens = authService.register(
                new RegisterRequest("ivan", "ivan@test.ru", "password123", Role.ADMIN));

        assertThat(storage.get("ivan").getRole()).isEqualTo(Role.ADMIN);
        assertThat(tokens.accessToken()).isEqualTo("access:ivan:ADMIN");
    }

    @Test
    void register_adminRequestedByNonFirstUser_throwsConflict() {
        storage.put("root", new User("root", "root@test.ru", "hash", Role.ADMIN));

        assertThatThrownBy(() -> authService.register(
                new RegisterRequest("ivan", "ivan@test.ru", "password123", Role.ADMIN)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("ADMIN");
        assertThat(storage).hasSize(1);
    }

    // --- Сценарии входа ---

    @Test
    void login_withCorrectPassword_returnsTokens() {
        String hash = passwordEncoder.encode("password123");
        storage.put("ivan", new User("ivan", "ivan@test.ru", hash, Role.USER));

        TokenResponse tokens = authService.login(new LoginRequest("ivan", "password123"));

        assertThat(tokens.accessToken()).isEqualTo("access:ivan:USER");
        assertThat(tokens.refreshToken()).isEqualTo("refresh:ivan:USER");
    }

    @Test
    void login_unknownUser_throwsBadCredentials() {
        assertThatThrownBy(() -> authService.login(new LoginRequest("ghost", "password123")))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void login_wrongPassword_throwsBadCredentials() {
        String hash = passwordEncoder.encode("password123");
        storage.put("ivan", new User("ivan", "ivan@test.ru", hash, Role.USER));

        assertThatThrownBy(() -> authService.login(new LoginRequest("ivan", "wrong-pass")))
                .isInstanceOf(BadCredentialsException.class);
    }

    // --- Сценарии refresh ---

    @Test
    void refresh_invalidToken_throwsBadCredentials() {
        jwtService.refreshValid = false;

        assertThatThrownBy(() -> authService.refresh("bad-token"))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void refresh_validToken_issuesNewPair() {
        storage.put("ivan", new User("ivan", "ivan@test.ru", "hash", Role.USER));
        jwtService.refreshValid = true;
        jwtService.refreshUser = "ivan";

        TokenResponse tokens = authService.refresh("good-token");

        assertThat(tokens.accessToken()).isEqualTo("access:ivan:USER");
        assertThat(tokens.refreshToken()).isEqualTo("refresh:ivan:USER");
    }
}
