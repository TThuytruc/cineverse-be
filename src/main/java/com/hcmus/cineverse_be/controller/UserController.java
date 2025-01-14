package com.hcmus.cineverse_be.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.hcmus.cineverse_be.request.RegisterRequest;
import com.hcmus.cineverse_be.response.BasicDataResponse;
import com.hcmus.cineverse_be.response.BasicResponse;
import com.hcmus.cineverse_be.response.auth.ProfileInformationResponse;
import com.hcmus.cineverse_be.response.auth.RefreshTokenResponse;
import com.hcmus.cineverse_be.response.auth.ValidationErrorResponse;
import com.hcmus.cineverse_be.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Tag(name = "User")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    // Register new user
    @Operation(
            summary = "Register new user",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Register successfully",
                            content = @Content(schema = @Schema(implementation = BasicResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validate input error",
                            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))
                    )
            }
    )
    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterRequest userRequest) {
        userService.create(userRequest.getUsername(), userRequest.getEmail(), userRequest.getPassword());
        BasicResponse response = new BasicResponse("User registered successfully! Check your email to activate your account.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
   }

    // Verify user token
    @Operation(
            summary = "Verify user token",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{ \"authenticated\": true }"
                                    )
                            )
                    )
            }
    )
    @GetMapping("/verify")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<Map<String, Boolean>> verifyToken(@RequestHeader("Authorization") String authorizationHeader) {
        Map<String, Boolean> response = new HashMap<>();
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                response.put("authenticated", false);
                return ResponseEntity.ok(response);
            }

            String token = authorizationHeader.substring(7);
            FirebaseAuth.getInstance().verifyIdToken(token);
            response.put("authenticated", true);
        } catch (FirebaseAuthException e) {
            response.put("authenticated", false);
        }

        return ResponseEntity.ok(response);
    }


    // Refresh user token
    @Operation(
            summary = "Refresh user token",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = RefreshTokenResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            content = @Content(schema = @Schema(implementation = BasicResponse.class))
                    )
            }
    )
    @PostMapping(value = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RefreshTokenResponse> refreshToken(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            schema = @Schema(
                                    type = "object",
                                    example = "{ \"refreshToken\": \"abc123xyz\" }"
                            )
                    )
            )
            @RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        RefreshTokenResponse response = userService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify-email-callback")
    public void emailVerificationCallback(@RequestParam String oobCode) {
        userService.verifyEmailCallback(oobCode);
    }

    @GetMapping("/user-info")
    public BasicDataResponse<ProfileInformationResponse> getUserInfo(@RequestParam("idToken") String idToken) {
        ProfileInformationResponse response = userService.getUserInformation(idToken);
        return BasicDataResponse.<ProfileInformationResponse>builder()
                .message("User information retrieved successfully.")
                .result(response)
                .build();
    }
}
