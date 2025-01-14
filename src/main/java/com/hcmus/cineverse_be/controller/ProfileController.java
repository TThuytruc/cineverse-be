package com.hcmus.cineverse_be.controller;

import com.hcmus.cineverse_be.dto.RatingDTO;
import com.hcmus.cineverse_be.dto.WatchListDTO;
import com.hcmus.cineverse_be.request.AddRatingRequest;
import com.hcmus.cineverse_be.request.AddWatchListRequest;
import com.hcmus.cineverse_be.response.BasicDataResponse;
import com.hcmus.cineverse_be.response.BasicResponse;
import com.hcmus.cineverse_be.response.movie.TrendingMoviesResponse;
import com.hcmus.cineverse_be.response.rating.RatingsResponse;
import com.hcmus.cineverse_be.response.rating.WatchListResponse;
import com.hcmus.cineverse_be.service.MovieService;
import com.hcmus.cineverse_be.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@Tag(name = "Profile")
@AllArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // Get user ratings
    @Operation(
            summary = "Get user ratings",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = RatingsResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid page",
                            content = @Content(schema = @Schema(implementation = BasicResponse.class))
                    )
            }
    )
    @GetMapping("/rating")
    @SecurityRequirement(name = "BearerAuth")
    public RatingsResponse getRatings(
            @RequestParam(defaultValue = "1") String page) {

        try {
            int pageNum = Integer.parseInt(page);
            return profileService.getRatingsByUser(pageNum);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid page: Page must be an integer.");
        }
    }


    // Get user watchlist
    @Operation(
            summary = "Get user watchlist",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = WatchListResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid page",
                            content = @Content(schema = @Schema(implementation = BasicResponse.class))
                    )
            }
    )
    @GetMapping("/watchlist")
    @SecurityRequirement(name = "BearerAuth")
    public WatchListResponse getWatchList(
            @RequestParam(defaultValue = "1") String page) {

        try {
            int pageNum = Integer.parseInt(page);
            return profileService.getWatchListByUser(pageNum);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid page: Page must be an integer.");
        }
    }


    // Add movie to watchlist
    @Operation(
            summary = "Add movie to watchlist",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Add successfully",
                            content = @Content(schema = @Schema(implementation = BasicDataResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Movie not found",
                            content = @Content(schema = @Schema(implementation = BasicResponse.class))
                    )
            }
    )
    @PostMapping("/watchlist")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<Object> addWatchList(@RequestBody AddWatchListRequest addWatchListRequest) {
        WatchListDTO result = profileService.addWatchList(addWatchListRequest.getMovieId());

        BasicDataResponse<WatchListDTO> response = new BasicDataResponse<>("Add to watchlist successfully.", result);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/watchlist/{movieId}")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<Object> deleteWatchList(@PathVariable long movieId) {
        profileService.deleteWatchList(movieId);

        BasicResponse response = new BasicResponse("Remove from watchlist successfully.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}