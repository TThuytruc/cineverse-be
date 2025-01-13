package com.hcmus.cineverse_be.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
@Setter
public class Rating {
    @Id
    private String _id;

    @Field("user_id")
    private String userId;

    @DBRef
    private MovieProfile movie;
//    private long movieId;

    @Field("create_at")
//    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();

    private int rating;
    private String review;
}
