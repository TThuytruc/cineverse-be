package com.hcmus.cineverse_be.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
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
            handleAuthenticationError(response, "Token is missing.");
            return;
        }

        token = token.substring(7);

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);

            FirebaseAuthenticationToken authenticationToken = new FirebaseAuthenticationToken(decodedToken);
            authenticationToken.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);
        } catch (FirebaseAuthException e) {
            if ("EXPIRED_ID_TOKEN".equals(e.getAuthErrorCode().toString())) {
                handleAuthenticationError(response, "Token has expired.");
            } else if ("INVALID_ID_TOKEN".equals(e.getAuthErrorCode().toString())) {
                handleAuthenticationError(response, "Invalid token.");
            } else {
                handleAuthenticationError(response, "Unauthorized.");
            }
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while authenticating.", e);
        }
    }

    private void handleAuthenticationError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"message\": \"" + message + "\"}");
    }
}
