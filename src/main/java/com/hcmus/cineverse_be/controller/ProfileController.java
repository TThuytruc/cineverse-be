package com.hcmus.cineverse_be.controller;

import com.hcmus.cineverse_be.dto.UserMovieDTO;
import com.hcmus.cineverse_be.request.AddFavoriteRequest;
import com.hcmus.cineverse_be.request.AddWatchListRequest;
import com.hcmus.cineverse_be.response.BasicDataResponse;
import com.hcmus.cineverse_be.response.BasicResponse;
import com.hcmus.cineverse_be.response.profile.FavoriteResponse;
import com.hcmus.cineverse_be.response.profile.RatingsResponse;
import com.hcmus.cineverse_be.response.profile.WatchListResponse;
import com.hcmus.cineverse_be.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
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
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Movie already exists in watchlist",
                            content = @Content(schema = @Schema(implementation = BasicResponse.class))
                    )
            }
    )
    @PostMapping("/watchlist")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<Object> addWatchList(@RequestBody AddWatchListRequest addWatchListRequest) {
        UserMovieDTO result = profileService.addWatchList(addWatchListRequest.getMovieId());

        BasicDataResponse<UserMovieDTO> response = new BasicDataResponse<>("Add to watchlist successfully.", result);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    // Delete a movie in watchlist
    @Operation(
            summary = "Delete a movie in watchlist",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Delete successfully",
                            content = @Content(schema = @Schema(implementation = BasicResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Movie not found",
                            content = @Content(schema = @Schema(implementation = BasicResponse.class))
                    )
            }
    )
    @DeleteMapping("/watchlist/{movieId}")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<Object> deleteWatchList(@PathVariable long movieId) {
        profileService.deleteWatchList(movieId);

        BasicResponse response = new BasicResponse("Remove from watchlist successfully.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    // Get user favorite movies
    @Operation(
            summary = "Get user favorite movies",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = FavoriteResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid page",
                            content = @Content(schema = @Schema(implementation = BasicResponse.class))
                    )
            }
    )
    @GetMapping("/favorite")
    @SecurityRequirement(name = "BearerAuth")
    public FavoriteResponse getFavorite(
            @RequestParam(defaultValue = "1") String page) {

        try {
            int pageNum = Integer.parseInt(page);
            return profileService.getFavoriteMoviesByUser(pageNum);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid page: Page must be an integer.");
        }
    }


    // Add movie to favorite list
    @Operation(
            summary = "Add movie to favorite list",
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
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Movie already exists in favorite list",
                            content = @Content(schema = @Schema(implementation = BasicResponse.class))
                    )
            }
    )
    @PostMapping("/favorite")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<Object> addFavorite(@RequestBody AddFavoriteRequest addFavoriteRequest) {
        UserMovieDTO result = profileService.addFavorite(addFavoriteRequest.getMovieId());

        BasicDataResponse<UserMovieDTO> response = new BasicDataResponse<>("Add to favorite list successfully.", result);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    // Delete a movie in favorite list
    @Operation(
            summary = "Delete a movie in favorite list",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Delete successfully",
                            content = @Content(schema = @Schema(implementation = BasicResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Movie not found",
                            content = @Content(schema = @Schema(implementation = BasicResponse.class))
                    )
            }
    )
    @DeleteMapping("/favorite/{movieId}")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<Object> deleteFavorite(@PathVariable long movieId) {
        profileService.deleteFavorite(movieId);

        BasicResponse response = new BasicResponse("Remove from favorite list successfully.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}