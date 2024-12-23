package com.hcmus.cineverse_be.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MovieDetailDTO {
    private long id;
    private String title;
    private String tagline;
    private String overview;
    private String posterPath;
    private String backdropPath;
    private double voteAverage;
    private int voteCount;
    private List<GenreDTO> genres;
    private String status;
    private String releaseDate;
    private long budget;
    private long revenue;
}
