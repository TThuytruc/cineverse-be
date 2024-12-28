package com.hcmus.cineverse_be.response.movie;

import com.hcmus.cineverse_be.dto.MovieSearchDTO;
import com.hcmus.cineverse_be.response.PaginationResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema
public class SearchMovieResponse extends PaginationResponse<MovieSearchDTO> {
    public SearchMovieResponse(int page, List<MovieSearchDTO> results, int totalPages, int totalResults) {
        super(page, results, totalPages, totalResults);
    }
}
