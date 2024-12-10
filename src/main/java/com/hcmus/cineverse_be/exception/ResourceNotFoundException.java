package com.hcmus.cineverse_be.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final String message;

    public ResourceNotFoundException(String msg) {
        super(msg);
        this.message = msg;
    }
}