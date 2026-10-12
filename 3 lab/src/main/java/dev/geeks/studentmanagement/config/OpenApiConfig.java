package dev.geeks.studentmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;

// =============================================================================
// Компонент:   OpenApiConfig
// Модуль:      dev.geeks.studentmanagement.config
// Назначение:  конфигурация Swagger/OpenAPI.
// Функции:     описание API; схема bearerAuth (JWT) и кнопка Authorize
//              в Swagger UI.
// Статус: реализовано.
// =============================================================================

// Конфигурация Swagger/OpenAPI: описание API и JWT security scheme,
// чтобы в Swagger UI появилась кнопка Authorize и можно было проверять роли.
@Configuration
public class OpenApiConfig {

    private static final String BEARER_AUTH_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI studentManagementOpenAPI() {
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        return new OpenAPI()
                .info(new Info()
                        .title("Student Management API")
                        .description("""
                                REST API системы управления студентами (лабораторная работа 3).

                                Возможности:
                                - JWT-авторизация: register, login, refresh (access + refresh токены);
                                - роли ADMIN и USER с разграничением на backend;
                                - чтение списка студентов с фильтрацией, сортировкой и пагинацией;
                                - создание, изменение и удаление студентов - только ролью ADMIN.

                                Для запросов к защищённым endpoints используйте Authorize \
                                и передавайте accessToken в заголовке Authorization: Bearer <token>.""")
                        .version("1.0"))
                .servers(List.of(new Server().url("http://localhost:8080").description("Локальный запуск")))
                .tags(List.of(new Tag().name("Auth").description("JWT-авторизация: регистрация, вход, обновление токенов")))
                .components(new Components().addSecuritySchemes(BEARER_AUTH_SCHEME, bearerAuth))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH_SCHEME));
    }
}
