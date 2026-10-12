package dev.geeks.studentmanagement.security;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import dev.geeks.studentmanagement.auth.model.Role;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// =============================================================================
// Компонент:   JwtAuthenticationFilter
// Модуль:      dev.geeks.studentmanagement.security
// Назначение:  JWT-фильтр аутентификации запросов (stateless).
// Функции:     извлечение access-токена из заголовка Authorization;
//              проверка токена и установка SecurityContext с ролью ROLE_*;
//              битый токен не прерывает запрос (401 вернёт SecurityConfig).
// Статус: реализовано.
// =============================================================================

// Фильтр безопасности: читает Authorization: Bearer <token>, проверяет access token
// и авторизует пользователя в SecurityContext. Работает до SecurityConfig-цепочки
// (addFilterBefore UsernamePasswordAuthenticationFilter) и stateless, сессия не используется.
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            // Публичные endpoints (auth, swagger) и запросы без токена пропускаем дальше.
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(BEARER_PREFIX.length()).trim();
        try {
            if (jwtService.validateAccessToken(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
                String username = jwtService.extractUsername(token);
                Role role = jwtService.extractRole(token);

                // ROLE_ префикс: SecurityConfig проверяет hasRole("ADMIN")/hasAnyRole("USER", "ADMIN").
                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
                var authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtException | IllegalArgumentException ex) {
            // Битый токен не должен ронять запрос: оставляем пользователя анонимным,
            // а SecurityConfig вернёт 401 на защищённый ресурс.
            SecurityContextHolder.clearContext();
            log.debug("JWT не прошёл проверку: {}", ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
