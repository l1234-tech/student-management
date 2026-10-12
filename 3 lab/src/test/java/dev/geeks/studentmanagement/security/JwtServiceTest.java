package dev.geeks.studentmanagement.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;

import dev.geeks.studentmanagement.auth.model.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

// Юнит-тесты JwtService: без Spring-контекста, чистая проверка логики токенов.
class JwtServiceTest {

    private static final String ACCESS_SECRET = "unit-test-access-secret-0123456789abcdef";
    private static final String REFRESH_SECRET = "unit-test-refresh-secret-0123456789abcdefgh";

    private JwtService service() {
        return new JwtService(ACCESS_SECRET, REFRESH_SECRET, 15, 30);
    }

    @Test
    void generateAccessToken_validatesAndExtractsClaims() {
        JwtService jwt = service();
        String token = jwt.generateAccessToken("admin", Role.ADMIN);

        assertThat(jwt.validateAccessToken(token)).isTrue();
        assertThat(jwt.extractUsername(token)).isEqualTo("admin");
        assertThat(jwt.extractRole(token)).isEqualTo(Role.ADMIN);
    }

    @Test
    void generateRefreshToken_validatesAndExtractsUsername() {
        JwtService jwt = service();
        String token = jwt.generateRefreshToken("reader", Role.USER);

        assertThat(jwt.validateRefreshToken(token)).isTrue();
        assertThat(jwt.extractUsernameFromRefresh(token)).isEqualTo("reader");
    }

    @Test
    void accessToken_isRejectedAsRefreshToken() {
        JwtService jwt = service();
        String accessToken = jwt.generateAccessToken("admin", Role.ADMIN);

        assertThat(jwt.validateRefreshToken(accessToken)).isFalse();
    }

    @Test
    void refreshToken_isRejectedAsAccessToken() {
        JwtService jwt = service();
        String refreshToken = jwt.generateRefreshToken("admin", Role.ADMIN);

        assertThat(jwt.validateAccessToken(refreshToken)).isFalse();
    }

    @Test
    void tokenSignedWithForeignSecret_isRejected() {
        JwtService jwt = service();
        SecretKey foreignKey = Keys.hmacShaKeyFor("foreign-secret-0123456789abcdefghij".getBytes(StandardCharsets.UTF_8));
        String foreignToken = Jwts.builder()
                .subject("hacker")
                .claim("role", Role.ADMIN.name())
                .claim("type", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(foreignKey)
                .compact();

        assertThat(jwt.validateAccessToken(foreignToken)).isFalse();
    }

    @Test
    void expiredToken_isRejected() {
        JwtService expired = new JwtService(ACCESS_SECRET, REFRESH_SECRET, -1, 30);
        String token = expired.generateAccessToken("admin", Role.ADMIN);

        assertThat(expired.validateAccessToken(token)).isFalse();
    }

    @Test
    void garbageToken_isRejected() {
        JwtService jwt = service();

        assertThat(jwt.validateAccessToken("not-a-jwt")).isFalse();
        assertThat(jwt.validateRefreshToken("not-a-jwt")).isFalse();
    }

    @Test
    void tokenWithoutRole_throwsOnExtractRole() {
        JwtService jwt = service();
        SecretKey key = Keys.hmacShaKeyFor(ACCESS_SECRET.getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .subject("admin")
                .claim("type", "access")
                .signWith(key)
                .compact();

        assertThatThrownBy(() -> jwt.extractRole(token))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
