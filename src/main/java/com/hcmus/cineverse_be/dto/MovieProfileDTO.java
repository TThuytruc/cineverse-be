package com.hcmus.cineverse_be.dto;

import com.hcmus.cineverse_be.entity.Genre;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
public class MovieProfileDTO {
    private long id;
    private String title;
    private String tagline;
    private String overview;
    private String posterPath;
    private String backdropPath;
    private double voteAverage;
    private int voteCount;
    private List<Genre> genres;
    private String status;
    private String releaseDate;
}
