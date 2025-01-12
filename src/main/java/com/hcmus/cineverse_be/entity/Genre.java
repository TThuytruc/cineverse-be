package com.hcmus.cineverse_be.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
//@Document(collection = "movie_genres")
public class Genre {
    @Id
    private String _id;

    @Field("id")
    private Integer id;

    private String name;

    @Field("tmdb_id")
    private Integer tmdbId;

}
