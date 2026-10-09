package dev.geeks.studentmanagement.config;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import dev.geeks.studentmanagement.common.error.ApiError;
import dev.geeks.studentmanagement.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Spring Security: stateless, JWT-фильтр, публичные auth/swagger endpoints и доступ по ролям.
// Разграничение прав выполняется ТОЛЬКО здесь, на backend: USER читает /api/requests/**,
// ADMIN может создавать, изменять и удалять.
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // endpoints без токена: авторизация, документация, dev-консоль H2, статика и служебный /error.
    private static final String[] PUBLIC_PATHS = {
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/refresh",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/api-docs/**",
            "/h2-console/**",
            "/",
            "/error",
            "/favicon.ico",
            "/css/**",
            "/js/**"
    };

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // REST + JWT: сессии не нужны, CSRF для stateless API отключается.
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // sameOrigin нужен для H2-консоли в dev (она грузится в iframe); в prod консоль выключена.
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_PATHS).permitAll()
                        // Чтение студентов доступно обеим ролям.
                        .requestMatchers(HttpMethod.GET, "/api/requests/**").hasAnyRole("USER", "ADMIN")
                        // Изменение данных - только ADMIN.
                        .requestMatchers(HttpMethod.POST, "/api/requests/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/requests/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/requests/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/requests/**").hasRole("ADMIN")
                        // Всё остальное требует валидный access-токен.
                        .anyRequest().authenticated())
                .exceptionHandling(handling -> handling
                        // 401: нет токена, токен невалиден или неверный логин/пароль - единый формат ApiError.
                        .authenticationEntryPoint(this::writeUnauthorized)
                        // 403: токен валиден, но роли не хватает прав.
                        .accessDeniedHandler(this::writeForbidden))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Пароль хранится и сравнивается только в виде хэша (BCrypt).
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private void writeUnauthorized(HttpServletRequest request,
                                   HttpServletResponse response,
                                   org.springframework.security.core.AuthenticationException ex) throws IOException {
        String message = (ex instanceof BadCredentialsException)
                ? "Неверный логин или пароль"
                : "Неверный или отсутствующий access-токен";
        writeError(response, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", message, request);
    }

    private void writeForbidden(HttpServletRequest request,
                                HttpServletResponse response,
                                org.springframework.security.access.AccessDeniedException ex) throws IOException {
        writeError(response, HttpStatus.FORBIDDEN, "FORBIDDEN",
                "Недостаточно прав: операция доступна только роли ADMIN", request);
    }

    // Единый формат ошибки API из common/error/ApiError.
    private void writeError(HttpServletResponse response,
                            HttpStatus status,
                            String code,
                            String message,
                            HttpServletRequest request) throws IOException {
        ApiError error = new ApiError(code, message, status.value(), Instant.now(),
                Map.of("path", String.valueOf(request.getRequestURI())));
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(toJson(error));
    }

    private String toJson(ApiError error) {
        // Минимальная сериализация без дополнительных зависимостей: поля record фиксированы.
        return "{\"code\":\"" + escape(error.code())
                + "\",\"message\":\"" + escape(error.message())
                + "\",\"status\":" + error.status()
                + ",\"timestamp\":\"" + error.timestamp()
                + "\",\"details\":" + detailsJson(error.details()) + "}";
    }

    private String detailsJson(Map<String, String> details) {
        if (details == null || details.isEmpty()) {
            return "null";
        }
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> entry : details.entrySet()) {
            if (!first) {
                json.append(",");
            }
            first = false;
            json.append("\"").append(escape(entry.getKey())).append("\":\"").append(escape(entry.getValue())).append("\"");
        }
        return json.append("}").toString();
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
