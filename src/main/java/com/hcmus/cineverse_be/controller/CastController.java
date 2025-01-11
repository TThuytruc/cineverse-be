package com.hcmus.cineverse_be.controller;

import java.security.cert.CertPathValidatorException.BasicReason;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hcmus.cineverse_be.dto.CastDetailDTO;
import com.hcmus.cineverse_be.response.cast.PopularCastResponse;
import com.hcmus.cineverse_be.service.CastService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/cast")
@Tag(name = "Cast")
public class CastController {
    private final CastService castService;

    public CastController(CastService castService) {
        this.castService = castService;
    }

    // Get popular casts
    @Operation(
            summary = "Get popular casts",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = PopularCastResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid page/Invalid period",
                            content = @Content(schema = @Schema(implementation = BasicReason.class))
                    )
            }
    )
    @GetMapping("/popular")
    public PopularCastResponse getTrendingMovies(
        @RequestParam(required = false) String query,
        @RequestParam(defaultValue = "1") String page) {
        try {
            int pageNum = Integer.parseInt(page);
            return castService.getPopularCast(query, pageNum);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid page: Page must be an integer.");
        }
    }

    // Get cast detail
    @Operation(
            summary = "Get cast detail",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = CastDetailDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid movie ID",
                            content = @Content(schema = @Schema(implementation = CastDetailDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Movie not found",
                            content = @Content(schema = @Schema(implementation = CastDetailDTO.class))
                    )
            }
    )
    @GetMapping("/{id}")
    public CastDetailDTO getMovieDetail(@PathVariable String id) {
        try {
            long castId = Long.parseLong(id);
            return castService.getCastDetail(castId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid ID: Cast ID must be an integer.");
        }
    }
}
