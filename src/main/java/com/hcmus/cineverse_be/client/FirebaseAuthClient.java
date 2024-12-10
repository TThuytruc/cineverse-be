package com.hcmus.cineverse_be.client;

import com.google.firebase.auth.FirebaseAuth;
import com.hcmus.cineverse_be.config.FirebaseConfigurationProperties;
import com.hcmus.cineverse_be.exception.FirebaseAuthenticationException;
import com.hcmus.cineverse_be.response.auth.RefreshTokenResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(FirebaseConfigurationProperties.class)
public class FirebaseAuthClient {
    private final FirebaseConfigurationProperties firebaseConfigurationProperties;
    private final FirebaseAuth firebaseAuth;
    private static final String REFRESH_TOKEN_URL = "https://securetoken.googleapis.com/v1/token";

    public RefreshTokenResponse refreshAccessToken(@NonNull final String refreshToken) {
        final var webApiKey = firebaseConfigurationProperties.getFirebase().getWebApiKey();
        final var requestBody = Map.of(
                "grant_type", "refresh_token",
                "refresh_token", refreshToken
        );

        System.out.println("Sending request with refreshToken: " + refreshToken);

        try {
            //System.out.println("webApiKey: " + webApiKey);
            final var response = RestClient.create(REFRESH_TOKEN_URL)
                    .post()
                    .uri(uriBuilder -> uriBuilder.queryParam("key", webApiKey).build())
                    .body(requestBody)
                    .contentType(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(RefreshTokenResponse.class);


            return RefreshTokenResponse.builder()
                    .id_token(response.getId_token())
                    .refresh_token(response.getRefresh_token())
                    .expires_in(response.getExpires_in())
                    .build();
        } catch (HttpClientErrorException exception) {
            System.err.println("Error refreshing token: " + exception.getResponseBodyAsString());
            throw new FirebaseAuthenticationException("Failed to refresh token.");
        }
    }
}
