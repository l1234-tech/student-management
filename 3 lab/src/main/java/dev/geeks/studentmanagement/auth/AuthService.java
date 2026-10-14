package dev.geeks.studentmanagement.auth;
import dev.geeks.studentmanagement.auth.dto.*;
import dev.geeks.studentmanagement.auth.model.*;
import dev.geeks.studentmanagement.auth.repository.*;
import dev.geeks.studentmanagement.common.exception.ConflictException;
import dev.geeks.studentmanagement.security.JwtService;
import java.time.Instant;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
@Service
public class AuthService {
    private final UserRepository users; private final RefreshSessionRepository sessions;
    private final PasswordEncoder passwords; private final JwtService jwt;
    public AuthService(UserRepository users,RefreshSessionRepository sessions,PasswordEncoder passwords,JwtService jwt){this.users=users;this.sessions=sessions;this.passwords=passwords;this.jwt=jwt;}
    @Transactional public TokenResponse register(RegisterRequest r){
        if(r.password().getBytes(java.nio.charset.StandardCharsets.UTF_8).length>72)throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,"Пароль не должен превышать 72 байта UTF-8");
        if(users.existsByUsername(r.username())||users.existsByEmail(r.email().toLowerCase(Locale.ROOT)))throw new ConflictException("Имя пользователя или email уже заняты");
        var u=new User();u.setUsername(r.username());u.setEmail(r.email().toLowerCase(Locale.ROOT));u.setPasswordHash(passwords.encode(r.password()));u.setRole(Role.USER);
        users.saveAndFlush(u);return issue(u);
    }
    @Transactional public TokenResponse login(LoginRequest r){
        var u=users.findByUsername(r.username()).or(()->users.findByEmail(r.username().toLowerCase(Locale.ROOT))).orElseThrow(this::unauthorized);
        if(!passwords.matches(r.password(),u.getPasswordHash()))throw unauthorized();
        return issue(u);
    }
    @Transactional public TokenResponse refresh(String token){
        try {
            var claims=jwt.validateRefreshToken(token);
            var session=sessions.findForRotation(claims.getId()).orElseThrow(this::unauthorized);
            if(session.isRevoked()||session.getExpiresAt().isBefore(Instant.now())||!session.getUsername().equals(claims.getSubject()))throw unauthorized();
            var u=users.findByUsername(claims.getSubject()).orElseThrow(this::unauthorized);
            session.revoke();sessions.saveAndFlush(session);return issue(u);
        } catch(io.jsonwebtoken.JwtException|IllegalArgumentException e){throw unauthorized();}
    }
    @Transactional public void logout(String token){
        try {var c=jwt.validateRefreshToken(token);sessions.findForRotation(c.getId()).ifPresent(s->{s.revoke();sessions.save(s);});}
        catch(io.jsonwebtoken.JwtException|IllegalArgumentException ignored){}
    }
    private TokenResponse issue(User u){var id=UUID.randomUUID().toString();var expiry=jwt.refreshExpiry();sessions.save(new RefreshSession(id,u.getUsername(),expiry));return new TokenResponse(jwt.generateAccessToken(u),jwt.generateRefreshToken(u,id,expiry));}
    private ResponseStatusException unauthorized(){return new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Неверные данные входа или токен");}
}
