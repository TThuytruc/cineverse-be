package com.hcmus.cineverse_be.controller;

import com.hcmus.cineverse_be.response.BasicResponse;
import com.hcmus.cineverse_be.response.movie.MovieDetailResponse;
import com.hcmus.cineverse_be.response.movie.SearchMoviesResponse;
import com.hcmus.cineverse_be.response.movie.TrendingMoviesResponse;
import com.hcmus.cineverse_be.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/movie")
@Tag(name = "Movie")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    // Get trending movies
    @Operation(
            summary = "Get trending movies",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = TrendingMoviesResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid page/Invalid period",
                            content = @Content(schema = @Schema(implementation = BasicResponse.class))
                    )
            }
    )
    @GetMapping("/trending")
    public Mono<TrendingMoviesResponse> getTrendingMovies(
            @Parameter(
                    schema = @Schema(allowableValues = {"day", "week"}, defaultValue = "day")
            )
            @RequestParam(defaultValue = "day") String period,
            @RequestParam(defaultValue = "1") int page) {

        return movieService.getTrending(period, page);
    }


    // Get movie detail
    @Operation(
            summary = "Get movie detail",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = MovieDetailResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Movie not found",
                            content = @Content(schema = @Schema(implementation = BasicResponse.class))
                    )
            }
    )
    @GetMapping("/{id}")
    public Mono<MovieDetailResponse> getMovieDetail(@PathVariable long id) {
        return movieService.getMovieDetail(id);
    }


    // Search movies
    @Operation(
            summary = "Search movies",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = SearchMoviesResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid page",
                            content = @Content(schema = @Schema(implementation = BasicResponse.class))
                    )
            }
    )
    @GetMapping("/search")
    public Mono<SearchMoviesResponse> getSearchMovies(
        @RequestParam(name = "query", required = false) String query,
        @RequestParam(defaultValue = "1") int page) {

        return movieService.getSearchMovies(query, page);
    }
}
