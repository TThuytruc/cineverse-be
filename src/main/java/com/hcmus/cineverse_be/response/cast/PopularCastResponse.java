package com.hcmus.cineverse_be.response.cast;

import java.util.List;

import com.hcmus.cineverse_be.dto.CastDTO;
import com.hcmus.cineverse_be.response.PaginationResponse;

public class PopularCastResponse extends PaginationResponse<CastDTO> {
    public PopularCastResponse(int page, List<CastDTO> results, int totalPages, int totalResults) {
        super(page, results, totalPages, totalResults);
    }
}
