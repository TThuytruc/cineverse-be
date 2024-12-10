package com.hcmus.cineverse_be.response.movie;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SearchMoviesResponse {
    private int page;
    private List<MovieDetailResponse> results;
    private int totalPages;
    private int totalResults;
}
