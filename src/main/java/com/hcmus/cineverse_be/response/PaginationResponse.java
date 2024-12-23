package com.hcmus.cineverse_be.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PaginationResponse<T> {
    private int page;
    private List<T> results;
    private int totalPages;
    private int totalResults;
}

