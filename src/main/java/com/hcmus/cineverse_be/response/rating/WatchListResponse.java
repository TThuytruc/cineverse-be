package com.hcmus.cineverse_be.response.rating;

import com.hcmus.cineverse_be.dto.RatingDTO;
import com.hcmus.cineverse_be.dto.WatchListDTO;
import com.hcmus.cineverse_be.response.PaginationResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema
public class WatchListResponse extends PaginationResponse<WatchListDTO> {
    public WatchListResponse(int page, List<WatchListDTO> results, int totalPages, int totalResults) {
        super(page, results, totalPages, totalResults);
    }
}
