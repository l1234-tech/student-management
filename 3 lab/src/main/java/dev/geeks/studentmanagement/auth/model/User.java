package dev.geeks.studentmanagement.auth.model;

// JPA entity пользователя: username, email, passwordHash, role.
// Пароль нельзя хранить в открытом виде, только hash.

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "app_users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false, unique = true, length = 50)
  @NotBlank
  @Pattern(regexp = "[A-Za-z0-9_]{3,50}")
  private String username;

  @Column(nullable = false, unique = true, length = 254)
  @NotBlank
  @Email
  @Size(max = 254)
  private String email;

  @Column(nullable = false)
  @NotBlank
  @Size(max = 255)
  private String passwordHash;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @NotNull
  private Role role;

  public Long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public Role getRole() {
    return role;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public void setRole(Role role) {
    this.role = role;
  }
}
