package com.hcmus.cineverse_be.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

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
    private LocalDateTime createdAt = LocalDateTime.now();

    private String review;
}
