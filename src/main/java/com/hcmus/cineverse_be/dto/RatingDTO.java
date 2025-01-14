package com.hcmus.cineverse_be.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RatingDTO {
    UserMovieDTO info;
    List<ReviewDTO> reviews;
}
