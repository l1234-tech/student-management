package dev.geeks.studentmanagement.auth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "refresh_sessions")
public class RefreshSession {
  @Id private String id;

  @Column(nullable = false)
  private String username;

  @Column(nullable = false)
  private Instant expiresAt;

  @Column(name = "revoked_at")
  private Instant revokedAt;

  // Read and maintain the previous flag during migration of existing databases.
  @Column(name = "revoked")
  private boolean legacyRevoked;

  protected RefreshSession() {}

  public RefreshSession(String id, String username, Instant expiresAt) {
    this.id = id;
    this.username = username;
    this.expiresAt = expiresAt;
  }

  public String getUsername() {
    return username;
  }

  public Instant getExpiresAt() {
    return expiresAt;
  }

  public Instant getRevokedAt() {
    return revokedAt;
  }

  public boolean isRevoked() {
    return revokedAt != null || legacyRevoked;
  }

  public void revoke() {
    if (revokedAt == null) {
      revokedAt = Instant.now();
    }
    legacyRevoked = true;
  }
}
