package com.hcmus.cineverse_be.service;

import com.hcmus.cineverse_be.exception.ResourceNotFoundException;
import com.hcmus.cineverse_be.response.movie.MovieDetailResponse;
import com.hcmus.cineverse_be.response.movie.TrendingMoviesResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private final String SMALL_POSTER_SIZE = "w342";
    private final String LARGE_POSTER_SIZE = "w780";

    @Value("${tmdb.api.base-image-url}")
    private String baseImageUrl;

    private final WebClient webClient;
    private String baseSmallPosterUrl;
    private String baseLargePosterUrl;


    public MovieService(WebClient webClient) {
        this.webClient = webClient;
    }

    @PostConstruct
    public void init() {
        this.baseSmallPosterUrl = baseImageUrl + SMALL_POSTER_SIZE;
        this.baseLargePosterUrl = baseImageUrl + LARGE_POSTER_SIZE;
    }

    public Mono<TrendingMoviesResponse> getTrending(String period, int page) {
        if (!"day".equals(period) && !"week".equals(period)) {
            throw new IllegalArgumentException("Invalid period: Period must be 'day' or 'week'.");
        }

        return webClient.get()
                .uri("trending/movie/" + period + "?page=" + page)
                .retrieve()
                .onStatus(
                        status -> status.value() == 400,
                        clientResponse -> clientResponse.bodyToMono(Map.class).flatMap(body -> {
                            if (body.containsKey("status_code")) {
                                int statusCode = (Integer) body.get("status_code");

                                if (statusCode == 22) {
                                    return Mono.error(new IllegalArgumentException((String) body.get("status_message")));
                                } else {
                                    return Mono.error(new RuntimeException("An error occurred while getting trending movies: " + body.get("status_message")));
                                }
                            }

                            return Mono.error(new RuntimeException("An error occurred while getting trending movies."));
                        })
                )
                .onStatus(
                        status -> status.value() != 200 && status.value() != 400,
                        clientResponse -> Mono.error(new RuntimeException("An error occurred while getting trending movies."))
                )
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(response -> {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
                    List<TrendingMoviesResponse.TrendingMovie> filteredData = results.stream()
                            .map(item -> {
                                TrendingMoviesResponse.TrendingMovie trendingMovie = new TrendingMoviesResponse.TrendingMovie();
                                trendingMovie.setId(((Number) item.get("id")).longValue());
                                trendingMovie.setTitle((String) item.get("title"));
                                trendingMovie.setPosterPath(baseSmallPosterUrl + item.get("poster_path"));
                                trendingMovie.setReleaseDate((String) item.get("release_date"));
                                trendingMovie.setVoteAverage((double) item.get("vote_average"));
                                trendingMovie.setVoteCount((int) item.get("vote_count"));

                                return trendingMovie;

                            })
                            .collect(Collectors.toList());

                    TrendingMoviesResponse trendingMoviesResponse = new TrendingMoviesResponse();
                    trendingMoviesResponse.setPage((int) response.get("page"));
                    trendingMoviesResponse.setResults(filteredData);
                    trendingMoviesResponse.setTotalPages((int) response.get("total_pages"));
                    trendingMoviesResponse.setTotalResults((int) response.get("total_results"));

                    return trendingMoviesResponse;
                });
    }

    public Mono<MovieDetailResponse> getMovieDetail(long movieId) {
        return webClient.get()
                .uri("movie/" + movieId)
                .retrieve()
                .onStatus(
                        status -> status.value() == 404,
                        clientResponse -> Mono.error(new ResourceNotFoundException("Movie not found."))
                )
                .onStatus(
                        status -> status.value() != 200 && status.value() != 404,
                        clientResponse -> Mono.error(new RuntimeException("An error occurred while getting movie detail."))
                )
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(response -> {
                    @SuppressWarnings("unchecked")
                    List<MovieDetailResponse.MovieGenre> genreList = (List<MovieDetailResponse.MovieGenre>) response.get("genres");

                    MovieDetailResponse movieDetailResponse = new MovieDetailResponse();

                    movieDetailResponse.setId(((Number) response.get("id")).longValue());
                    movieDetailResponse.setTitle((String) response.get("title"));
                    movieDetailResponse.setTagline((String) response.get("tagline"));
                    movieDetailResponse.setOverview((String) response.get("overview"));
                    movieDetailResponse.setPosterPath(baseLargePosterUrl + response.get("poster_path"));
                    movieDetailResponse.setVoteAverage((double) response.get("vote_average"));
                    movieDetailResponse.setVoteCount((int) response.get("vote_count"));
                    movieDetailResponse.setGenres(genreList);
                    movieDetailResponse.setStatus((String) response.get("status"));
                    movieDetailResponse.setReleaseDate((String) response.get("release_date"));
                    movieDetailResponse.setBudget(((Number) response.get("budget")).longValue());
                    movieDetailResponse.setRevenue(((Number) response.get("revenue")).longValue());

                    return movieDetailResponse;
                });
    }
}
