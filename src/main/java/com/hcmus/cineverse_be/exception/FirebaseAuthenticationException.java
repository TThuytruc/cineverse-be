package com.hcmus.cineverse_be.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

//@Component
@Getter
public class FirebaseAuthenticationException extends AuthenticationException {
    private final String message;

    public FirebaseAuthenticationException(String msg) {
        super(msg);
        this.message = msg;
    }
}
