// представляет пользователя и описывает какие мы вообще данные о нем храним в бд

package dev.geeks.studentmanagement.auth.model;

import jakarta.persistence.*;

@Entity
@Table(name = "app_users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false, unique = true, length = 50)
  private String username;

  @Column(nullable = false, unique = true, length = 254)
  private String email;

  @Column(nullable = false)
  private String passwordHash;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
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

  public void setUsername(String v) {
    username = v;
  }

  public void setEmail(String v) {
    email = v;
  }

  public void setPasswordHash(String v) {
    passwordHash = v;
  }

  public void setRole(Role v) {
    role = v;
  }
}
