package com.hcmus.cineverse_be.entity;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimilarMovies {

    @Field("tmdb_id")
    private Integer tmdbId;

    @Field("similar_movies")
    private List<MovieDetail> results;

}