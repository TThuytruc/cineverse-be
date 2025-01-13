package com.hcmus.cineverse_be.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
@Document(collection = "movies")
public class MovieProfile {
    @Id
    private String _id;

    @Field("id")
    private long id;

    private String title;
    private String tagline;
    private String overview;

    @Field("poster_path")
    private String posterPath;

    @Field("backdrop_path")
    private String backdropPath;

    @Field("vote_average")
    private double voteAverage;

    @Field("vote_count")
    private int voteCount;

    private List<Genre> genres;

    private String status;

    @Field("release_date")
    private String releaseDate;
}
