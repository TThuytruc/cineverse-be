package com.hcmus.cineverse_be.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewDTO {
    private String userId;
    private long movieId;
    private String createdAt;
    private String review;
}
