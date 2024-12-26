package com.hcmus.cineverse_be.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieSearch {
    @Id
    private String _id;

    @Field("id")
    private long id;

    private String title;

    @Field("poster_path")
    private String posterPath;

    @Field("backdrop_path")
    private String backdropPath;

    @Field("release_date")
    private String releaseDate;

    @Field("vote_average")
    private double voteAverage;

    @Field("vote_count")
    private int voteCount;
}
