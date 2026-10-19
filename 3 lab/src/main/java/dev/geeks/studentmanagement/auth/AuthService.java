package dev.geeks.studentmanagement.auth;

import dev.geeks.studentmanagement.auth.dto.LoginRequest;
import dev.geeks.studentmanagement.auth.dto.RefreshTokenRequest;
import dev.geeks.studentmanagement.auth.dto.RegisterRequest;
import dev.geeks.studentmanagement.auth.dto.TokenResponse;
import dev.geeks.studentmanagement.auth.model.RefreshSession;
import dev.geeks.studentmanagement.auth.model.Role;
import dev.geeks.studentmanagement.auth.model.User;
import dev.geeks.studentmanagement.auth.repository.RefreshSessionRepository;
import dev.geeks.studentmanagement.auth.repository.UserRepository;
import dev.geeks.studentmanagement.common.exception.ConflictException;
import dev.geeks.studentmanagement.security.JwtService;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserRepository users;
    private final RefreshSessionRepository sessions;
    private final PasswordEncoder passwords;
    private final JwtService jwt;

    public AuthService(
            UserRepository users,
            RefreshSessionRepository sessions,
            PasswordEncoder passwords,
            JwtService jwt) {
        this.users = users;
        this.sessions = sessions;
        this.passwords = passwords;
        this.jwt = jwt;
    }

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Пароль не должен превышать 72 байта UTF-8");
        }
        String email = request.email().toLowerCase(Locale.ROOT);
        if (users.existsByUsername(request.username()) || users.existsByEmail(email)) {
            throw new ConflictException("Имя пользователя или email уже заняты");
        }

        var user = new User();
        user.setUsername(request.username());
        user.setEmail(email);
        user.setPasswordHash(passwords.encode(request.password()));
        user.setRole(Role.USER);
        users.saveAndFlush(user);
        return issue(user);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        var user = users.findByUsername(request.username())
                .or(() -> users.findByEmail(request.username().toLowerCase(Locale.ROOT)))
                .orElseThrow(this::unauthorized);
        if (!passwords.matches(request.password(), user.getPasswordHash())) {
            throw unauthorized();
        }
        return issue(user);
    }

    @Transactional
    public TokenResponse refresh(String token) {
        try {
            var claims = jwt.validateRefreshToken(token);
            var session = sessions.findForRotation(claims.getId())
                    .orElseThrow(this::unauthorized);
            if (session.isRevoked()
                    || !session.getExpiresAt().isAfter(Instant.now())
                    || !session.getUsername().equals(claims.getSubject())) {
                throw unauthorized();
            }

            var user = users.findByUsername(claims.getSubject()).orElseThrow(this::unauthorized);
            session.revoke();
            sessions.saveAndFlush(session);
            return issue(user);
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException exception) {
            throw unauthorized();
        }
    }

    @Transactional
    public void logout(String token) {
        try {
            var claims = jwt.validateRefreshToken(token);
            sessions.findForRotation(claims.getId()).ifPresent(session -> {
                session.revoke();
                sessions.save(session);
            });
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException ignored) {
            // Logout is idempotent: invalid or already-revoked tokens require no action.
        }
    }

    @Transactional
    public int cleanupExpiredSessions(Instant cutoff) {
        return sessions.deleteExpiredBefore(cutoff);
    }

    private TokenResponse issue(User user) {
        var id = UUID.randomUUID().toString();
        var expiry = jwt.refreshExpiry();
        sessions.save(new RefreshSession(id, user.getUsername(), expiry));
        return new TokenResponse(
                jwt.generateAccessToken(user),
                jwt.generateRefreshToken(user, id, expiry));
    }

    private ResponseStatusException unauthorized() {
        return new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Неверные данные входа или токен");
    }
}
