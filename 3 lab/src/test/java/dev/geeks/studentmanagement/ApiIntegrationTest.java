package dev.geeks.studentmanagement;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.databind.*;
import dev.geeks.studentmanagement.auth.repository.UserRepository;
import dev.geeks.studentmanagement.security.JwtService;
import java.time.*;
@SpringBootTest(properties={"spring.datasource.url=${LAB_TEST_DB_URL:jdbc:h2:mem:labtest;MODE=PostgreSQL;DB_CLOSE_DELAY=-1}","spring.datasource.username=${LAB_TEST_DB_USER:sa}","spring.datasource.password=${LAB_TEST_DB_PASSWORD:}","app.admin.username=testadmin","app.admin.email=admin@test.local"})
@org.springframework.test.context.TestExecutionListeners(listeners=org.springframework.test.context.support.DependencyInjectionTestExecutionListener.class,mergeMode=org.springframework.test.context.TestExecutionListeners.MergeMode.REPLACE_DEFAULTS)
@AutoConfigureMockMvc
class ApiIntegrationTest {
    // Disposable credentials are generated for this test run, never taken from the user's database.
    private static final String TEST_PASSWORD = "Test-" + java.util.UUID.randomUUID();
    private static final String ACCESS_SECRET = java.util.UUID.randomUUID().toString() + java.util.UUID.randomUUID();
    private static final String REFRESH_SECRET = java.util.UUID.randomUUID().toString() + java.util.UUID.randomUUID();
    @org.springframework.test.context.DynamicPropertySource
    static void testSecrets(org.springframework.test.context.DynamicPropertyRegistry registry) {
        registry.add("app.admin.password", () -> TEST_PASSWORD);
        registry.add("app.jwt.access-secret", () -> ACCESS_SECRET);
        registry.add("app.jwt.refresh-secret", () -> REFRESH_SECRET);
    }

    @Autowired MockMvc mvc; @Autowired ObjectMapper json; @Autowired UserRepository users; @Autowired JwtService jwt;
    private String access;
    private JsonNode send(MockHttpServletRequestBuilder r,int status) throws Exception {
        var result=mvc.perform(r.contentType("application/json")).andExpect(status().is(status)).andReturn();
        var body=result.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
        return (body.isBlank()||body.stripLeading().startsWith("<"))?json.createObjectNode():json.readTree(body);
    }
    private MockHttpServletRequestBuilder auth(MockHttpServletRequestBuilder r){return r.header("Authorization","Bearer "+access);}
    private String student(int id,int room){return "{\"ISU\":"+id+",\"name\":\"Маша Иванова\",\"group\":\"P1234\",\"dormitory\":5,\"room\":"+room+",\"checkin_date\":\"2026-09-01\",\"is_foreign\":false,\"note\":\"тест\"}";}
    @Test void completeLifecycleAndPermissions() throws Exception {
        send(get("/api/requests"),401);
        send(post("/api/auth/register").content("{\"username\":\"newuser\",\"email\":\"not-email\",\"password\":\"short\"}"),422);
        var registered=send(post("/api/auth/register").content("{\"username\":\"newuser\",\"email\":\"user@test.local\",\"password\":\""+TEST_PASSWORD+"\"}"),201);
        access=registered.get("accessToken").asText();String refresh=registered.get("refreshToken").asText();
        assertTrue(users.findByUsername("newuser").orElseThrow().getPasswordHash().startsWith("$2"));
        send(post("/api/auth/register").content("{\"username\":\"newuser\",\"email\":\"user@test.local\",\"password\":\""+TEST_PASSWORD+"\"}"),409);
        send(post("/api/auth/login").content("{\"username\":\"newuser\",\"password\":\"wrong\"}"),401);
        send(auth(get("/api/requests")),200);
        send(auth(post("/api/requests").content(student(123456,1234))),403);
        send(auth(put("/api/requests/123456").content(student(123456,1234))),403);
        send(auth(patch("/api/requests/123456").content("{\"room\":5678}")),403);
        send(auth(delete("/api/requests/123456")),403);
        var rotated=send(post("/api/auth/refresh").content("{\"refreshToken\":\""+refresh+"\"}"),200);
        send(post("/api/auth/refresh").content("{\"refreshToken\":\""+refresh+"\"}"),401);
        send(post("/api/auth/refresh").content("{\"refreshToken\":\""+access+"\"}"),401);
        String rotatedRefresh=rotated.get("refreshToken").asText();
        send(post("/api/auth/logout").content("{\"refreshToken\":\""+rotatedRefresh+"\"}"),204);
        send(post("/api/auth/refresh").content("{\"refreshToken\":\""+rotatedRefresh+"\"}"),401);
        access=registered.get("refreshToken").asText();send(auth(get("/api/requests")),401);
        var login=send(post("/api/auth/login").content("{\"username\":\"testadmin\",\"password\":\""+TEST_PASSWORD+"\"}"),200);
        access=login.get("accessToken").asText();
        assertEquals("ADMIN",send(auth(get("/api/auth/me")),200).get("role").asText());
        send(auth(post("/api/requests").content(student(123456,1234))),201);
        send(auth(post("/api/requests").content(student(123456,1234))),409);
        assertEquals(1,send(auth(get("/api/requests").param("group","  P1234  ").param("name","  Маша Иванова  ")),200).get("totalElements").asInt());
        assertEquals(0,send(auth(get("/api/requests").param("group","   ")),200).get("totalElements").asInt());
        assertEquals(0,send(auth(get("/api/requests").param("name","")),200).get("totalElements").asInt());
        send(auth(patch("/api/requests/123456").content("{\"name\":\"  Маша Иванова  \",\"group\":\"  P1234  \"}")),200);
        send(auth(patch("/api/requests/123456").content("{\"name\":\"   \"}")),422);

        send(auth(post("/api/requests").content(student(234567,1))),422);
        send(auth(post("/api/requests").content(student(234567,1234).replace("2026-09-01","2099-09-01"))),422);
        assertEquals(123456,send(auth(get("/api/requests/123456")),200).get("data").get("ISU").asInt());
        send(auth(patch("/api/requests/123456").content("{\"room\":5678,\"note\":\"\"}")),200);
        assertEquals(5678,send(auth(get("/api/requests/123456")),200).get("data").get("room").asInt());
        send(auth(patch("/api/requests/123456").content("{\"room\":1}")),422);
        send(auth(patch("/api/requests/123456").content("{\"ISU\":234567}")),400);
        send(auth(put("/api/requests/123456").content(student(123456,4321))),200);
        send(auth(put("/api/requests/123456").content(student(234567,4321))),400);
        send(auth(post("/api/requests").content(student(234567,1234))),201);
        var page=send(auth(get("/api/requests?sort=ISU,desc&size=1&page=0")),200);
        assertEquals(234567,page.get("data").get(0).get("ISU").asInt());assertEquals(2,page.get("totalElements").asInt());
        assertEquals(1,send(auth(get("/api/requests?room=4321&is_foreign=false")),200).get("totalElements").asInt());
        send(auth(get("/api/requests?size=0")),400);send(auth(get("/api/requests?sort=bad")),400);
        var query=send(auth(request("QUERY",java.net.URI.create("/api/requests")).content("{\"room\":4321}")),200);
        assertEquals(1,query.get("totalElements").asInt());
        send(auth(post("/api/requests/search").content("{\"room\":4321}")),200);
        send(auth(delete("/api/requests/123456")),204);send(auth(get("/api/requests/123456")),404);
        send(auth(delete("/api/requests/234567")),204);
        send(get("/"),200);send(get("/login/"),200);send(get("/add-student/"),200);send(get("/student/"),200);
        send(get("/v3/api-docs"),200);
    }
}
