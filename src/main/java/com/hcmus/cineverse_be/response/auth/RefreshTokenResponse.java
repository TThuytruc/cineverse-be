package com.hcmus.cineverse_be.response.auth;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshTokenResponse {
    String id_token;
    String refresh_token;
    String expires_in;
}
