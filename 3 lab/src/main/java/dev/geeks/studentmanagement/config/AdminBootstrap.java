// создаем админов из настроек окружения
// админская версия AuthService , где построение админов

package dev.geeks.studentmanagement.config;

import dev.geeks.studentmanagement.auth.dto.RegisterRequest;
import dev.geeks.studentmanagement.auth.model.*;
import dev.geeks.studentmanagement.auth.repository.UserRepository;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements CommandLineRunner {
  private final UserRepository users;
  private final PasswordEncoder passwords;
  private final Validator validator;

  @Value("${app.admin.username:}")
  private String username;

  @Value("${app.admin.email:}")
  private String email;

  @Value("${app.admin.password:}")
  private String password;

  public AdminBootstrap(UserRepository users, PasswordEncoder passwords, Validator validator) {
    this.users = users;
    this.passwords = passwords;
    this.validator = validator;
  }

  public void run(String... args) {
    if (username.isBlank()) return;

    if (!validator.validate(new RegisterRequest(username, email, password)).isEmpty())
      throw new IllegalArgumentException("Invalid ADMIN configuration");

    if (users.existsByUsername(username)) return;

    if (users.existsByEmail(email.toLowerCase(java.util.Locale.ROOT)))
      throw new IllegalArgumentException("ADMIN email already exists");

    var u = new User();

    u.setUsername(username);
    u.setEmail(email.toLowerCase(java.util.Locale.ROOT));
    u.setPasswordHash(passwords.encode(password));
    u.setRole(Role.ADMIN);
    users.saveAndFlush(u);

    org.slf4j.LoggerFactory.getLogger(getClass())
        .info("Created configured administrator {}", username);
  }
}
