package com.hcmus.cineverse_be.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Component
@ConfigurationProperties(prefix = "com.hcmus")
public class FirebaseConfigurationProperties {
    @Valid
    private FireBase firebase = new FireBase();

    @Getter
    @Setter
    public static class FireBase {
        @NotBlank(message = "Firestore private key must be configured")
        private String privateKey;

        @NotBlank(message = "Firebase Web API key must be configured")
        private String webApiKey;
    }
}
