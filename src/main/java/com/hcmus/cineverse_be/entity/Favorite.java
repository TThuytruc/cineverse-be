package com.hcmus.cineverse_be.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
public class Favorite {
    @Id
    private String _id;

    @Field("user_id")
    private String userId;

    @DBRef
    private MovieProfile movie;
}
