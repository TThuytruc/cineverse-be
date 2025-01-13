package com.hcmus.cineverse_be.controller;

import com.hcmus.cineverse_be.dto.GenreDTO;
import com.hcmus.cineverse_be.dto.LastestTrailersDTO;
import com.hcmus.cineverse_be.dto.MovieDetailDTO;
import com.hcmus.cineverse_be.dto.MovieTrendingDTO;
import com.hcmus.cineverse_be.entity.Genre;
import com.hcmus.cineverse_be.entity.MovieTrending;
import com.hcmus.cineverse_be.response.BasicResponse;
import com.hcmus.cineverse_be.response.PaginationResponse;
import com.hcmus.cineverse_be.response.movie.SearchMovieResponse;
import com.hcmus.cineverse_be.response.movie.TrendingMoviesResponse;
import com.hcmus.cineverse_be.response.retriever.RetrieverResponse;
import com.hcmus.cineverse_be.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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
        @RequestParam(required = false) String query,
        @RequestParam(required = false) String fromDate,
        @RequestParam(required = false) String toDate,
        @RequestParam(required = false) List<Integer> withGenres,
        @RequestParam(defaultValue = "1") String page) {

        try {
            int pageNum = Integer.parseInt(page);
            return movieService.getSearchMovies(query, pageNum, fromDate, toDate, withGenres);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid page: Page must be an integer.");
        }
    }

    @Operation(
            summary = "Get llm search movies",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = RetrieverResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid ",
                            content = @Content(schema = @Schema(implementation = BasicResponse.class))
                    )
            }
    )

    @GetMapping("/llm-movie-search")
    public SearchMovieResponse getLLMMovieSearch(
            @RequestParam("collectionName") String collectionName,
            @RequestParam("query") String query,
            @RequestParam(name = "amount", defaultValue = "10") Integer amount,
            @RequestParam(name = "threshold", defaultValue = "0.25") Double threshold) {

        try {

            return movieService.getMoviesFromLlmRetriever(
                    collectionName,
                    query,
                    amount,
                    threshold);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid parameters in llm movie search.");
        }
    }

    @Operation(
            summary = "Get ai navigation",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = Object.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid ",
                            content = @Content(schema = @Schema(implementation = BasicResponse.class))
                    )
            }
    )
    @GetMapping("/ai-navigation")
    public Object getAiNavigation(@RequestParam("query") String query) {
        try {
            return movieService.getAINavigation(query);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid parameters in ai navigation.");
        }
    }

    @Operation(
            summary = "Get all genres",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = GenreDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid page/Invalid period",
                            content = @Content(schema = @Schema(implementation = BasicResponse.class))
                    )
            }
    )
    @GetMapping("/genres")
    public List<GenreDTO> getAllGenres() {
        return movieService.getAllGenres();
    }

    @Operation(
            summary = "Get lastest trailer",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = GenreDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid page/Invalid period",
                            content = @Content(schema = @Schema(implementation = BasicResponse.class))
                    )
            }
    )
    @GetMapping("/latest-trailer")
    public List<LastestTrailersDTO> getLatestTrailer() {
        return movieService.getLastestTrailers();
    }

    @Operation(
            summary = "Get popular movies",
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
    @GetMapping("/popular")
    public List<MovieTrendingDTO> getPopularMovies(
            @RequestParam(defaultValue = "1") String page) {

        try {
            int pageNum = Integer.parseInt(page);
            return movieService.getMoviePopular(pageNum);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid page: Page must be an integer.");
        }
    }
}
