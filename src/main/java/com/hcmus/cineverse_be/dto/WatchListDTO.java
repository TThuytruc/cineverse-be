package com.hcmus.cineverse_be.dto;

import com.hcmus.cineverse_be.entity.MovieProfile;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WatchListDTO {
    private String _id;
    private String userId;
    private MovieProfileDTO movie;
}