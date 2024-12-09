package com.hcmus.cineverse_be.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Component
@ConfigurationProperties(prefix = "com.example")
public class FirebaseConfigurationProperties {
    @Valid
    private FireBase firebase = new FireBase();

    @Getter
    @Setter
    public static class FireBase {
        @Value("${com.example.firebase.private-key}")
        @NotBlank(message = "Firestore private key must be configured")
        private String privateKey;

        @Value("${com.example.firebase.web-api-key}")
        @NotBlank(message = "Firebase Web API key must be configured")
        private String webApiKey;
    }
}