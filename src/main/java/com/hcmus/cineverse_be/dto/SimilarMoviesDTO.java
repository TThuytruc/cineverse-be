package com.hcmus.cineverse_be.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimilarMoviesDTO {
    private Integer tmdbId;
    private List<MovieDetailDTO> results;
}
