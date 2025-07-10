package com.centralService.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();


        if (path.startsWith("/login") || path.startsWith("/register") || path.startsWith("/auth")
                || path.startsWith("/css") || path.startsWith("/js") || path.startsWith("/images")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            String accept = request.getHeader("Accept");

            if (accept != null && accept.contains("text/html")) {
                response.sendRedirect("/login");
            } else {

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized");
            }
            return;
        }


        filterChain.doFilter(request, response);
    }
}
