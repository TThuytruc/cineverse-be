package com.hcmus.cineverse_be.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
public class CastDetail {
    @Field("id")
    private long id;

    private String name;
    private String biography;
    private String birthday;
    private int gender;

    @Field("place_of_birth")
    private String placeOfBirth;

    @Field("known_for_department")
    private String knownFor;

    @Field("profile_path")
    private String profilePath;

    @Field("movie_credits.cast")
    List<MovieDetail> movieCredits;
}
