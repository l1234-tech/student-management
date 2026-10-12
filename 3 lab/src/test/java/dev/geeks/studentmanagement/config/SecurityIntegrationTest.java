package dev.geeks.studentmanagement.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.http.HttpClient;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.jayway.jsonpath.JsonPath;

// Интеграционный тест безопасности: реальный сервер (RANDOM_PORT), H2 в памяти.
// Проверяет реальную цепочку SecurityConfig + JwtAuthenticationFilter:
// публичные auth-эндпойнты, 401 без токена, 403 для роли USER, Swagger.
// Используется буферизованный RestTemplate: TestRestTemplate работает в
// streaming-mode и падает на POST, отвечающий 401 (HttpRetryException).
// Модуль студентов не используется: GET /api/requests проверяется как
// "проходит безопасность" (не 401/403), независимо от наличия контроллера.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecurityIntegrationTest {

    @LocalServerPort
    private int port;

    private RestTemplate rest;
    private String adminUsername;
    private String adminToken;
    private String refreshToken;
    private String userToken;

    @BeforeAll
    void initClientsAndUsers() {
        // JdkClientHttpRequestFactory (java.net.http): HttpURLConnection падает
        // на POST, отвечающий 401, с "cannot retry ... in streaming mode".
        rest = new RestTemplate(new JdkClientHttpRequestFactory(HttpClient.newHttpClient()));
        // 4xx/4xx-ответы не должны превращаться в исключения — их тело проверяется в тестах.
        rest.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) {
                // не используется: hasError всегда false
            }
        });

        // Первый пользователь системы получает роль ADMIN.
        adminUsername = "it-admin-" + suffix();
        ResponseEntity<String> admin = post("/api/auth/register",
                "{\"username\":\"" + adminUsername + "\",\"email\":\"" + adminUsername + "@test.ru\","
                        + "\"password\":\"password123\",\"role\":\"ADMIN\"}");
        assertThat(admin.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        adminToken = JsonPath.read(admin.getBody(), "$.accessToken");
        refreshToken = JsonPath.read(admin.getBody(), "$.refreshToken");

        // Второй пользователь — всегда роль USER.
        String userName = "it-user-" + suffix();
        ResponseEntity<String> user = post("/api/auth/register",
                "{\"username\":\"" + userName + "\",\"email\":\"" + userName + "@test.ru\","
                        + "\"password\":\"password123\"}");
        assertThat(user.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        userToken = JsonPath.read(user.getBody(), "$.accessToken");
    }

    private static String suffix() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    private HttpEntity<String> jsonEntity(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    private HttpEntity<String> bearerJsonEntity(String token, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return new HttpEntity<>(body, headers);
    }

    private HttpEntity<String> bearerEntity(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return new HttpEntity<>(headers);
    }

    private ResponseEntity<String> post(String path, String body) {
        return rest.postForEntity(url(path), jsonEntity(body), String.class);
    }

    private ResponseEntity<String> get(String path, String token) {
        return rest.exchange(url(path), HttpMethod.GET, bearerEntity(token), String.class);
    }

    private ResponseEntity<String> postAuthorized(String path, String token, String body) {
        return rest.exchange(url(path), HttpMethod.POST, bearerJsonEntity(token, body), String.class);
    }

    // --- Сценарии ---

    @Test
    void login_withValidCredentials_returns200_tokens() {
        ResponseEntity<String> response = post("/api/auth/login",
                "{\"username\":\"" + adminUsername + "\",\"password\":\"password123\"}");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<String>read(response.getBody(), "$.accessToken")).isNotNull();
    }

    @Test
    void login_withWrongPassword_returns401_ApiError() {
        ResponseEntity<String> response = post("/api/auth/login",
                "{\"username\":\"ghost-" + suffix() + "\",\"password\":\"wrong-pass\"}");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(JsonPath.<String>read(response.getBody(), "$.code")).isEqualTo("UNAUTHORIZED");
    }

    @Test
    void protectedEndpoint_withoutToken_returns401_ApiError() {
        ResponseEntity<String> response = rest.getForEntity(url("/api/requests"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(JsonPath.<String>read(response.getBody(), "$.code")).isEqualTo("UNAUTHORIZED");
    }

    @Test
    void protectedEndpoint_withValidToken_passesSecurity() {
        ResponseEntity<String> response = get("/api/requests", adminToken);

        // 401/403 быть не должно: токен валиден, роль подходит.
        // 404 ожидаем, пока модуль студентов не реализован; после его появления станет 200.
        assertThat(response.getStatusCode()).isNotIn(HttpStatus.UNAUTHORIZED, HttpStatus.FORBIDDEN);
    }

    @Test
    void mutatingEndpoint_withUserRole_returns403_ApiError() {
        ResponseEntity<String> response = postAuthorized("/api/requests", userToken,
                "{\"ISU\":1,\"name\":\"x\",\"group\":\"g\"}");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(JsonPath.<String>read(response.getBody(), "$.code")).isEqualTo("FORBIDDEN");
    }

    @Test
    void refresh_withValidToken_returns200_newTokens() {
        ResponseEntity<String> response = post("/api/auth/refresh",
                "{\"refreshToken\":\"" + refreshToken + "\"}");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<String>read(response.getBody(), "$.accessToken")).isNotNull();
    }

    @Test
    void register_duplicateUsername_returns409_ApiError() {
        String name = "dup-" + suffix();
        ResponseEntity<String> first = post("/api/auth/register",
                "{\"username\":\"" + name + "\",\"email\":\"" + name + "@test.ru\","
                        + "\"password\":\"password123\"}");
        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<String> second = post("/api/auth/register",
                "{\"username\":\"" + name + "\",\"email\":\"other-" + name + "@test.ru\","
                        + "\"password\":\"password123\"}");
        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(JsonPath.<String>read(second.getBody(), "$.code")).isEqualTo("CONFLICT");
    }

    @Test
    void register_weakPassword_returns422_ApiError() {
        ResponseEntity<String> response = post("/api/auth/register",
                "{\"username\":\"weak-" + suffix() + "\",\"email\":\"weak-" + suffix() + "@test.ru\","
                        + "\"password\":\"short\"}");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(JsonPath.<String>read(response.getBody(), "$.code")).isEqualTo("VALIDATION_ERROR");
    }

    @Test
    void swaggerApiDocs_exposeAuthEndpoints() {
        ResponseEntity<String> docs = rest.getForEntity(url("/v3/api-docs"), String.class);

        assertThat(docs.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(docs.getBody()).contains("/api/auth/register", "/api/auth/login", "/api/auth/refresh");
    }

    @Test
    void swaggerUi_isReachable() {
        ResponseEntity<String> ui = rest.getForEntity(url("/swagger-ui/index.html"), String.class);

        assertThat(ui.getStatusCode().is2xxSuccessful()).isTrue();
    }
}
