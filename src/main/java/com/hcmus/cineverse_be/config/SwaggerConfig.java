package com.hcmus.cineverse_be.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
        info = @Info(
                title = "Cineverse API",
                description = "<h4>Movie recommendation using AI<h4>"
        )
)
@SecurityScheme(
        name = "BearerAuth",
        description = "ID token from Firebase",
        scheme = "bearer",
        type= SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {
}
