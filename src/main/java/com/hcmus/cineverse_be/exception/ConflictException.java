package com.hcmus.cineverse_be.exception;

import lombok.Getter;

@Getter
public class ConflictException extends RuntimeException {
    private final String message;

    public ConflictException(String msg) {
        super(msg);
        this.message = msg;
    }
}