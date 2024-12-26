package com.hcmus.cineverse_be.controller;

import com.hcmus.cineverse_be.dto.MovieDetailDTO;
import com.hcmus.cineverse_be.dto.MovieTrendingDTO;
import com.hcmus.cineverse_be.entity.MovieTrending;
import com.hcmus.cineverse_be.response.BasicResponse;
import com.hcmus.cineverse_be.response.PaginationResponse;
import com.hcmus.cineverse_be.response.movie.SearchMovieResponse;
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
    public TrendingMoviesResponse getTrendingMovies(
            @Parameter(
                    schema = @Schema(allowableValues = {"day", "week"}, defaultValue = "day")
            )
            @RequestParam(defaultValue = "day") String period,
            @RequestParam(defaultValue = "1") String page) {

        try {
            int pageNum = Integer.parseInt(page);
            return movieService.getTrending(period, pageNum);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid page: Page must be an integer.");
        }
    }


    // Get movie detail
    @Operation(
            summary = "Get movie detail",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = MovieDetailDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid movie ID",
                            content = @Content(schema = @Schema(implementation = BasicResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Movie not found",
                            content = @Content(schema = @Schema(implementation = BasicResponse.class))
                    )
            }
    )
    @GetMapping("/{id}")
    public MovieDetailDTO getMovieDetail(@PathVariable String id) {
        try {
            long movieId = Long.parseLong(id);
            return movieService.getMovieDetail(movieId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid ID: Movie ID must be an integer.");
        }
    }

    // Get search movies
    @Operation(
            summary = "Get search movies",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = SearchMovieResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid page/Invalid period",
                            content = @Content(schema = @Schema(implementation = BasicResponse.class))
                    )
            }
    )
    @GetMapping("/search")
    public SearchMovieResponse getSearchMovieResponse(
        @RequestParam String query,
        @RequestParam(defaultValue = "1") String page) {

        try {
            int pageNum = Integer.parseInt(page);
            return movieService.getSearchMovies(query, pageNum);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid page: Page must be an integer.");
        }
    }

}
