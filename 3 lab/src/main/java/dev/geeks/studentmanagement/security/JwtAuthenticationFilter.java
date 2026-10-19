package dev.geeks.studentmanagement.security;

import dev.geeks.studentmanagement.auth.repository.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtService jwt;
  private final UserRepository users;

  public JwtAuthenticationFilter(JwtService jwt, UserRepository users) {
    this.jwt = jwt;
    this.users = users;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      try {
        var claims = jwt.validateAccessToken(header.substring(7));
        users
            .findByUsername(claims.getSubject())
            .ifPresent(
                u -> {
                  var auth =
                      new UsernamePasswordAuthenticationToken(
                          u.getUsername(),
                          null,
                          List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole().name())));
                  SecurityContextHolder.getContext().setAuthentication(auth);
                });
      } catch (io.jsonwebtoken.JwtException | IllegalArgumentException ignored) {
        SecurityContextHolder.clearContext();
      }
    }
    chain.doFilter(request, response);
  }
}
