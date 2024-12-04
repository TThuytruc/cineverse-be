package com.hcmus.cineverse_be.response;

import lombok.Getter;

@Getter
public class BasicResponse {
    private final String message;

    public BasicResponse(String message) {
        this.message = message;
    }
}
