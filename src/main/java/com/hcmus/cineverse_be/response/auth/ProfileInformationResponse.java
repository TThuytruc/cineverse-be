package com.hcmus.cineverse_be.response.auth;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileInformationResponse {
    String username;
    String email;
    String uid;
    long createdAt;
}

/*
    String photoUrl;
    String phoneNumber;
    String displayName;
    String createdAt;
    String lastLoginAt;
    String emailVerified;
    String disabled;*/