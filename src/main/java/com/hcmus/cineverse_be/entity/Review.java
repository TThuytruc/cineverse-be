package com.hcmus.cineverse_be.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
public class Review {
    @Id
    private String _id;

    @Field("user_id")
    private String userId;

    @Field("movie_id")
    private long movieId;

    @Field("create_at")
    private String createdAt = Instant.now().truncatedTo(ChronoUnit.MILLIS).toString();

    private String review;
}
