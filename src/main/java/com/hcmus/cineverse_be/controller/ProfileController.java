package com.hcmus.cineverse_be.controller;

import com.hcmus.cineverse_be.response.BasicResponse;
import com.hcmus.cineverse_be.response.movie.TrendingMoviesResponse;
import com.hcmus.cineverse_be.response.rating.RatingsResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}