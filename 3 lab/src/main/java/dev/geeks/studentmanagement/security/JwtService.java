package dev.geeks.studentmanagement.security;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import dev.geeks.studentmanagement.auth.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

// =============================================================================
// Компонент:   JwtService
// Модуль:      dev.geeks.studentmanagement.security
// Назначение:  создание и проверка JWT (jjwt 0.12.x).
// Функции:     генерация access/refresh токенов с разными секретами;
//              проверка подписи, срока действия и типа токена;
//              извлечение username и роли из claims.
// Статус: реализовано.
// =============================================================================

// Сервис JWT (jjwt 0.12.x): создание access/refresh токенов, проверка подписи и срока,
// чтение username и роли из claims. Access и refresh подписаны разными секретами,
// а claim "type" не даёт использовать access-токен как refresh-токен и наоборот.
@Component
public class JwtService {

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_TYPE = "type";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    private final SecretKey accessKey;
    private final SecretKey refreshKey;
    private final Duration accessTtl;
    private final Duration refreshTtl;

    public JwtService(@Value("${app.jwt.access-secret}") String accessSecret,
                      @Value("${app.jwt.refresh-secret}") String refreshSecret,
                      @Value("${app.jwt.access-ttl-minutes}") long accessTtlMinutes,
                      @Value("${app.jwt.refresh-ttl-days}") long refreshTtlDays) {
        // jjwt требует HMAC-ключ не короче 256 бит (32 байта) - секреты в application.yml длинее.
        this.accessKey = Keys.hmacShaKeyFor(accessSecret.getBytes(StandardCharsets.UTF_8));
        this.refreshKey = Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
        this.accessTtl = Duration.ofMinutes(accessTtlMinutes);
        this.refreshTtl = Duration.ofDays(refreshTtlDays);
    }

    public String generateAccessToken(String username, Role role) {
        return createToken(username, role, accessKey, accessTtl, TYPE_ACCESS);
    }

    public String generateRefreshToken(String username, Role role) {
        return createToken(username, role, refreshKey, refreshTtl, TYPE_REFRESH);
    }

    // Проверка access-токена: подпись, срок действия и тип токена.
    public boolean validateAccessToken(String token) {
        return isValid(token, accessKey, TYPE_ACCESS);
    }

    // Проверка refresh-токена: подпись, срок действия и тип токена.
    public boolean validateRefreshToken(String token) {
        return isValid(token, refreshKey, TYPE_REFRESH);
    }

    public String extractUsername(String token) {
        return parse(token, accessKey).getSubject();
    }

    public String extractUsernameFromRefresh(String token) {
        return parse(token, refreshKey).getSubject();
    }

    // Роль пользователя из claims access-токена: SecurityConfig проверяет её на backend.
    public Role extractRole(String token) {
        Object role = parse(token, accessKey).get(CLAIM_ROLE);
        if (role == null) {
            throw new IllegalArgumentException("В токене нет роли пользователя");
        }
        return Role.valueOf(role.toString());
    }

    private String createToken(String username, Role role, SecretKey key, Duration ttl, String type) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .claim(CLAIM_ROLE, role.name())
                .claim(CLAIM_TYPE, type)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(ttl)))
                .signWith(key)
                .compact();
    }

    private boolean isValid(String token, SecretKey key, String expectedType) {
        try {
            Claims claims = parse(token, key);
            Object type = claims.get(CLAIM_TYPE);
            return expectedType.equals(type);
        } catch (JwtException | IllegalArgumentException ex) {
            // Просроченный, чужой или подделанный токен: считаем проверку не пройденной.
            return false;
        }
    }

    private Claims parse(String token, SecretKey key) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
