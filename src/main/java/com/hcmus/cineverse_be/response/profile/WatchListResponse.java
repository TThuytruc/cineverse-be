package com.hcmus.cineverse_be.response.profile;

import com.hcmus.cineverse_be.dto.UserMovieDTO;
import com.hcmus.cineverse_be.response.PaginationResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema
public class WatchListResponse extends PaginationResponse<UserMovieDTO> {
    public WatchListResponse(int page, List<UserMovieDTO> results, int totalPages, int totalResults) {
        super(page, results, totalPages, totalResults);
    }
}
