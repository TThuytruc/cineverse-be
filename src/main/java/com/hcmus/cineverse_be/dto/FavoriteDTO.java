package com.hcmus.cineverse_be.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavoriteDTO {
    private String _id;
    private String userId;
    private MovieProfileDTO movie;
}