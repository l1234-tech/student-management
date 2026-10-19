package dev.geeks.studentmanagement.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.geeks.studentmanagement.common.error.ApiError;
import dev.geeks.studentmanagement.security.JwtAuthenticationFilter;
import java.time.Instant;
import java.util.Map;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public FilterRegistrationBean<JwtAuthenticationFilter> jwtRegistration(
      JwtAuthenticationFilter filter) {
    var registration = new FilterRegistrationBean<>(filter);
    registration.setEnabled(false);
    return registration;
  }

  @Bean
  public SecurityFilterChain security(
      HttpSecurity http, JwtAuthenticationFilter jwt, ObjectMapper mapper) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/api/auth/register",
                        "/api/auth/login",
                        "/api/auth/refresh",
                        "/api/auth/logout",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html")
                    .permitAll()
                    .requestMatchers("/api/user/**")
                    .hasAnyRole("USER", "ADMIN")
                    .requestMatchers("/api/admin/**")
                    .hasRole("ADMIN")
                    .requestMatchers("/api/**")
                    .authenticated()
                    .anyRequest()
                    .permitAll())
        .exceptionHandling(
            errors ->
                errors
                    .authenticationEntryPoint(
                        (request, response, exception) -> {
                          response.setStatus(401);
                          response.setContentType("application/json;charset=UTF-8");
                          mapper.writeValue(
                              response.getOutputStream(),
                              Map.of(
                                  "error",
                                  new ApiError(
                                      "UNAUTHORIZED",
                                      "Войдите в систему",
                                      401,
                                      Instant.now(),
                                      Map.of())));
                        })
                    .accessDeniedHandler(
                        (request, response, exception) -> {
                          response.setStatus(403);
                          response.setContentType("application/json;charset=UTF-8");
                          mapper.writeValue(
                              response.getOutputStream(),
                              Map.of(
                                  "error",
                                  new ApiError(
                                      "FORBIDDEN",
                                      "Недостаточно прав",
                                      403,
                                      Instant.now(),
                                      Map.of())));
                        }))
        .addFilterBefore(jwt, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
