package com.hcmus.cineverse_be.response.movie;

import com.hcmus.cineverse_be.dto.MovieTrendingDTO;
import com.hcmus.cineverse_be.response.PaginationResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema
public class TrendingMoviesResponse extends PaginationResponse<MovieTrendingDTO> {
    public TrendingMoviesResponse(int page, List<MovieTrendingDTO> results, int totalPages, int totalResults) {
        super(page, results, totalPages, totalResults);
    }
}

