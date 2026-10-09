package dev.geeks.studentmanagement.auth;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.geeks.studentmanagement.auth.dto.LoginRequest;
import dev.geeks.studentmanagement.auth.dto.RegisterRequest;
import dev.geeks.studentmanagement.auth.dto.TokenResponse;
import dev.geeks.studentmanagement.auth.model.Role;
import dev.geeks.studentmanagement.auth.model.User;
import dev.geeks.studentmanagement.auth.repository.UserRepository;
import dev.geeks.studentmanagement.common.exception.ConflictException;
import dev.geeks.studentmanagement.security.JwtService;

// Сервис авторизации: регистрация, вход, обновление токена.
// Пароль хранится только в виде BCrypt-хэша, наружу пароль не возвращается никогда.
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        // Проверка корректности данных: уникальность username и email на уровне сервиса,
        // уникальность также защищена ограничениями @UniqueConstraint в User.
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Пользователь с таким username уже зарегистрирован");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Пользователь с таким email уже зарегистрирован");
        }

        Role role = resolveRole(request.role());
        User user = new User(request.username(), request.email(), passwordEncoder.encode(request.password()), role);
        userRepository.save(user);

        return issueTokens(user);
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Неверный логин или пароль"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Неверный логин или пароль");
        }
        return issueTokens(user);
    }

    @Transactional(readOnly = true)
    public TokenResponse refresh(String refreshToken) {
        // Проверяется подпись refresh-секретом, срок действия и тип токена.
        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw new BadCredentialsException("Недействительный или просроченный refresh-токен");
        }
        User user = userRepository.findByUsername(jwtService.extractUsernameFromRefresh(refreshToken))
                .orElseThrow(() -> new BadCredentialsException("Недействительный или просроченный refresh-токен"));
        return issueTokens(user);
    }

    // Сценарий ролей: по умолчанию USER; роль ADMIN можно получить только первому
    // пользователю системы, чтобы нельзя было зарегистрировать администратора в обход контроля.
    private Role resolveRole(Role requestedRole) {
        if (requestedRole == null || requestedRole == Role.USER) {
            return Role.USER;
        }
        if (userRepository.count() == 0) {
            return Role.ADMIN;
        }
        throw new ConflictException("Роль ADMIN может получить только первый пользователь системы");
    }

    private TokenResponse issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user.getUsername(), user.getRole());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername(), user.getRole());
        return new TokenResponse(accessToken, refreshToken);
    }
}
