package com.hcmus.cineverse_be.controller;

import com.hcmus.cineverse_be.response.BasicResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@Tag(name = "Test Security")
public class TestController {

    // Public endpoint
    @Operation(
            summary = "Testing public endpoint",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                examples = @ExampleObject(
                                        value = "Public Endpoint"
                                )
                            )
                    )
            }
    )
    @GetMapping("/public")
    public String publicEndpoint() {
        return "Public Endpoint";
    }


    // Private endpoint
    @Operation(
            summary = "Testing private endpoint",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = "Private Endpoint"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Token is missing/Invalid token/Token has expired/...",
                            content = @Content(schema = @Schema(implementation = BasicResponse.class))
                    )
            }
    )
    @GetMapping("/private")
    @SecurityRequirement(name = "BearerAuth")
    public String privateEndpoint() {
        return "Private Endpoint";
    }
}
