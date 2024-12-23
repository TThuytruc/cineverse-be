package com.hcmus.cineverse_be.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieTrendingDTO {
    private long id;
    private String title;
    private String posterPath;
    private String releaseDate;
    private double voteAverage;
    private int voteCount;
}
