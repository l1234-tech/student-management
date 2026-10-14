package dev.geeks.studentmanagement.auth.model;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="refresh_sessions")
public class RefreshSession {
    @Id private String id;
    @Column(nullable=false) private String username;
    @Column(nullable=false) private Instant expiresAt;
    private boolean revoked;
    public RefreshSession(){}
    public RefreshSession(String id,String username,Instant expiresAt){this.id=id;this.username=username;this.expiresAt=expiresAt;}
    public String getUsername(){return username;}
    public Instant getExpiresAt(){return expiresAt;}
    public boolean isRevoked(){return revoked;}
    public void revoke(){revoked=true;}
}
