package com.hcmus.cineverse_be.dto;

import com.hcmus.cineverse_be.entity.MovieProfile;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserMovieDTO {
    private String userId;
    private MovieProfile movie;
    private Integer rating;
    private boolean isFavorite;
    private boolean inWatchList;
}
