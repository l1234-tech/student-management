// контроллер HTTP API авторизации 
// клиент отправил запрос - здесь принимаем его и передаем сервису - возвращаем результат клиенту 

package dev.geeks.studentmanagement.auth;

import dev.geeks.studentmanagement.auth.dto.*;
import dev.geeks.studentmanagement.auth.repository.UserRepository;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService service;
  private final UserRepository users;

  public AuthController(AuthService service, UserRepository users) {
    this.service = service;
    this.users = users;
  }

  @PostMapping("/register")
  @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
  public TokenResponse register(@Valid @RequestBody RegisterRequest r) {
    return service.register(r);
  }

  @PostMapping("/login")
  public TokenResponse login(@Valid @RequestBody LoginRequest r) {
    return service.login(r);
  }

  @PostMapping("/refresh")
  public TokenResponse refresh(@Valid @RequestBody RefreshTokenRequest r) {
    return service.refresh(r.refreshToken());
  }

  @PostMapping("/logout")
  @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
  public void logout(@Valid @RequestBody RefreshTokenRequest r) {
    service.logout(r.refreshToken());
  }

  @GetMapping("/me")
  public Map<String, String> me(Principal principal) {
    var u = users.findByUsername(principal.getName()).orElseThrow();
    return Map.of("username", u.getUsername(), "role", u.getRole().name());
  }
}
