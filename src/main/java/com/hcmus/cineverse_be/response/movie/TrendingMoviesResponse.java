package com.hcmus.cineverse_be.response.movie;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TrendingMoviesResponse {
    private int page;
    private List<TrendingMovie> results;
    private int totalPages;
    private int totalResults;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TrendingMovie {
        private long id;
        private String title;
        private String posterPath;
        private String releaseDate;
        private double voteAverage;
        private int voteCount;
    }
}
