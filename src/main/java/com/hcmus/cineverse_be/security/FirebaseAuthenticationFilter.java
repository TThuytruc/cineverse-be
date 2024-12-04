package com.hcmus.cineverse_be.security;

import com.hcmus.cineverse_be.exception.FirebaseAuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            FirebaseAuthenticationEntryPoint entryPoint = new FirebaseAuthenticationEntryPoint();
            entryPoint.commence(request, response, new FirebaseAuthenticationException("Token is missing."));
            return;
        }

        token = token.substring(7);

        FirebaseAuthenticationToken authenticationToken =
                new FirebaseAuthenticationToken(token);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }
}
