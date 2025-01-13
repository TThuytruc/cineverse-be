package com.hcmus.cineverse_be.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BasicDataResponse<T> {
    private String message;
    private T result;
}
