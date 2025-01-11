package com.hcmus.cineverse_be.dto;

import java.util.List;

import com.hcmus.cineverse_be.entity.MovieDetail;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CastDetailDTO {
    private long id;
    private String name;
    private String biography;
    private String birthday;
    private int gender;
    private String placeOfBirth;
    private String knownFor;
    private String profilePath;
    List<MovieDetail> movieCredits;
}
