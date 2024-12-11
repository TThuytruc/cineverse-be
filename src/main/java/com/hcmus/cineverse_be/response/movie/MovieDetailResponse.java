package com.hcmus.cineverse_be.response.movie;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MovieDetailResponse {
    private long id;
    private String title;
    private String tagline;
    private String overview;
    private String posterPath;
    private String backdropPath;
    private double voteAverage;
    private int voteCount;
    private List<MovieGenre> genres;
    private String status;
    private String releaseDate;
    private long budget;
    private long revenue;
    private String backdropPath;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class MovieGenre {
        private int id;
        private String name;
    }
}
