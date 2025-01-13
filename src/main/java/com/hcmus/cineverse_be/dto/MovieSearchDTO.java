package com.hcmus.cineverse_be.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieSearchDTO {
    private long id;
    private String title;
    private String posterPath;
    private String backdropPath;
    private String releaseDate;
    private double voteAverage;
    private int voteCount;
    private List<GenreDTO> genres;
}
