package dev.geeks.studentmanagement.security;
import dev.geeks.studentmanagement.auth.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
@Service
public class JwtService {
    private final SecretKey accessKey,refreshKey;
    private final long accessMinutes,refreshDays;
    public JwtService(@Value("${app.jwt.access-secret:}") String access,@Value("${app.jwt.refresh-secret:}") String refresh,
        @Value("${app.jwt.access-ttl-minutes:15}") long minutes,@Value("${app.jwt.refresh-ttl-days:30}") long days) throws Exception {
        accessKey=key(access,"access.key");refreshKey=key(refresh,"refresh.key");accessMinutes=minutes;refreshDays=days;
        if(Arrays.equals(accessKey.getEncoded(),refreshKey.getEncoded()))throw new IllegalArgumentException("JWT keys must differ");
    }
    private SecretKey key(String configured,String file) throws Exception {
        if(!configured.isBlank())return Keys.hmacShaKeyFor(configured.getBytes(StandardCharsets.UTF_8));
        Path path=Path.of("data",file);Files.createDirectories(path.getParent());
        if(!Files.exists(path)){
            byte[] bytes=new byte[32];new java.security.SecureRandom().nextBytes(bytes);
            try {Files.writeString(path,Base64.getEncoder().encodeToString(bytes),StandardOpenOption.CREATE_NEW);}
            catch(FileAlreadyExistsException ignored){}
        }
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(Files.readString(path).trim()));
    }
    public Instant refreshExpiry(){return Instant.now().plus(refreshDays,ChronoUnit.DAYS);}
    public String generateAccessToken(User u){return token(u,"access",UUID.randomUUID().toString(),Instant.now().plus(accessMinutes,ChronoUnit.MINUTES),accessKey);}
    public String generateRefreshToken(User u,String id,Instant expiry){return token(u,"refresh",id,expiry,refreshKey);}
    private String token(User u,String type,String id,Instant expiry,SecretKey key){
        return Jwts.builder().subject(u.getUsername()).id(id).claim("role",u.getRole().name()).claim("type",type)
            .issuedAt(new Date()).expiration(Date.from(expiry)).signWith(key).compact();
    }
    public Claims validateAccessToken(String token){return read(token,"access",accessKey);}
    public Claims validateRefreshToken(String token){return read(token,"refresh",refreshKey);}
    private Claims read(String token,String type,SecretKey key){
        return Jwts.parser().verifyWith(key).require("type",type).build().parseSignedClaims(token).getPayload();
    }
}
