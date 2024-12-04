package com.hcmus.cineverse_be.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FirebaseAuthenticationProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        FirebaseAuthenticationToken token = (FirebaseAuthenticationToken) authentication;

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(String.valueOf(token.getFirebaseToken()));
            FirebaseAuthenticationToken authenticationResult = new FirebaseAuthenticationToken(decodedToken);
            authenticationResult.setAuthenticated(true);
            return authenticationResult;

        } catch (FirebaseAuthException e) {
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            try {
                if ("EXPIRED_ID_TOKEN".equals(e.getAuthErrorCode().toString())) {
                    response.getWriter().write("{\"message\": \"Token has expired.\"}");
                } else if ("INVALID_ID_TOKEN".equals(e.getAuthErrorCode().toString())) {
                    response.getWriter().write("{\"message\": \"Invalid token.\"}");
                } else {
                    response.getWriter().write("{\"message\": \"Unauthorized.\"}");
                }

                return null;
            } catch(Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(FirebaseAuthenticationToken.class);
    }
}
