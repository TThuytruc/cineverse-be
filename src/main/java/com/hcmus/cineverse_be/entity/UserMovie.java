package com.hcmus.cineverse_be.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserMovie {
    @Id
    private String _id;

    @Field("user_id")
    private String userId;

    @DBRef
    private MovieProfile movie;

    @Field(write = Field.Write.ALWAYS)
    private Integer rating;

    @Field(name="is_favorite", write = Field.Write.ALWAYS)
    private boolean isFavorite;

    @Field(name="in_watchlist", write = Field.Write.ALWAYS)
    private boolean inWatchList;
}
