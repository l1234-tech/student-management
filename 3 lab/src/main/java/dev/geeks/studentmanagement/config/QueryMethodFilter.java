package dev.geeks.studentmanagement.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Maps the legacy QUERY method to the supported JSON search endpoint. */
@Component
@Order(-200)
public class QueryMethodFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    if ("QUERY".equals(request.getMethod())
        && "/api/user/students".equals(request.getRequestURI())) {
      chain.doFilter(
          new HttpServletRequestWrapper(request) {
            @Override
            public String getMethod() {
              return "POST";
            }

            @Override
            public String getRequestURI() {
              return "/api/user/students/search";
            }

            @Override
            public String getServletPath() {
              return "/api/user/students/search";
            }
          },
          response);
      return;
    }
    chain.doFilter(request, response);
  }
}
