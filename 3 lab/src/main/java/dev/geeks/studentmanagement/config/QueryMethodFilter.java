package dev.geeks.studentmanagement.config;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
@Component @Order(-200)
public class QueryMethodFilter extends OncePerRequestFilter {
    protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain) throws ServletException,IOException {
        if("QUERY".equals(request.getMethod())&&"/api/requests".equals(request.getRequestURI())) {
            chain.doFilter(new HttpServletRequestWrapper(request) {
                public String getMethod(){return "POST";}
                public String getRequestURI(){return "/api/requests/search";}
                public String getServletPath(){return "/api/requests/search";}
            },response);
        } else chain.doFilter(request,response);
    }
}
