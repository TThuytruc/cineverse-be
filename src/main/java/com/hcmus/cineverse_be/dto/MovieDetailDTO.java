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
    private List<Integer> genreIds;
    private List<CastDTO> cast;
    private List<CrewDTO> crew;
    private String status;
    private String releaseDate;
    private long budget;
    private long revenue;
    private List<ReviewDTO> reviews;
}
