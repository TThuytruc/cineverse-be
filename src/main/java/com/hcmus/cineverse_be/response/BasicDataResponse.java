package com.hcmus.cineverse_be.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class BasicDataResponse<T> {
    private String message;
    private T result;
}
